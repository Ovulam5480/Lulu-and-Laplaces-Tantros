package lulu.lib.singularity.game.researchs;

import arc.struct.*;
import lulu.*;
import lulu.lib.singularity.world.blocks.research.*;
import lulu.type.resourceStacks.*;
import mindustry.ctype.*;

public class ResearchManager{
  private final ArrayMap<String, ResearchGroup> allProjects = new ArrayMap<>();

  public ResearchGroup makeGroup(String zoneKey){
    ResearchGroup group = new ResearchGroup(zoneKey);
    allProjects.put(zoneKey, group);
    return group;
  }

  public ResearchGroup getGroup(String zoneKey){
    return allProjects.get(zoneKey);
  }

  public ResearchGroup getOrCreateGroup(String zoneKey){
    ResearchGroup group = allProjects.get(zoneKey);
    if(group == null){
      group = new ResearchGroup(zoneKey);
      allProjects.put(zoneKey, group);
    }
    return group;
  }

  public Seq<ResearchProject> currentListResearches(){
    ResearchGroup group = allProjects.getValueAt(currentType());
    return group == null ? new Seq<>() : group.listResearches();
  }

  public void init(){
    allProjects.values().forEach(ResearchGroup::init);
  }

  public void reset(){
    allProjects.values().forEach(ResearchGroup::reset);
  }

  public void save(){
    allProjects.values().forEach(ResearchGroup::save);
  }

  public void load(){
    allProjects.values().forEach(ResearchGroup::load);
  }

  public ResearchGroup current(){
    return LuluMod.researches.getGroup(allProjects.getKeyAt(currentType()));
  }

  public int currentType(){
    return 0;//todo rules
  }

  /**
   * 判断内容是否已在当前区域解锁（主要用于 BOSS 区块的专属解锁）。全局解锁由 Mindustry 自身处理。
   */
  public boolean isUnlocked(UnlockableContent content){
    ResearchGroup group = current();
    return group != null && group.scopedUnlocks.contains(content);
  }

  /**
   * 研究树定义用的内部 DSL（领域特定语言）辅助类。
   *
   * <p>用法：新建一个类继承 {@code ResearchSDL}（如 {@code SglResearchs}），在 {@code init()} 中
   * 用下面这些静态方法描述整棵研究树。所有方法均为 {@code protected static}，子类可直接继承调用。
   *
   * <h3>两层结构：区域（zone） → 研究项目（project）</h3>
   * <ul>
   *   <li>区域由 {@link #makeContext(String, Runnable)} 划定，对应一个 {@link ResearchGroup}（zoneKey，
   *       例如 {@code "water"} 或 {@code "boss:1"}）。一个区域下可定义多个研究项目。</li>
   *   <li>研究项目由 {@link #research(String, int)} 系列方法创建，挂到当前区域下。</li>
   * </ul>
   *
   * <h3>两阶段回放机制（为什么 {@code research} 要带 Runnable）</h3>
   * 由于 {@code contents / dependencies / devices ...} 这些方法是“无主语”的静态调用，它们必须知道
   * “当前正在配置哪个项目”。DSL 用一组静态字段维护这个状态：
   * <ul>
   *   <li>{@code context}：当前区域（ResearchGroup）。</li>
   *   <li>{@code currProject}：当前正在配置的项目。</li>
   *   <li>{@code inProjectContext}：是否正处于某项目的配置回调中。</li>
   *   <li>{@code tasks}：暂存的“项目 → 配置回调”映射。</li>
   *   <li>{@code currReveal}：当前揭示分组（揭示门控）的栈顶。</li>
   * </ul>
   * 流程为：
   * <ol>
   *   <li>在 {@code makeContext} 的 runnable 里调用 {@code research(... , Runnable)}，此时只登记项目并把
   *       配置回调存入 {@code tasks}（此时还没有项目上下文）。</li>
   *   <li>{@code makeContext} 跑完 runnable 后，遍历 {@code tasks}，逐个把 {@code currProject} 设为该项目、
   *       置 {@code inProjectContext = true}，再执行其回调——于是回调里的 {@code contents/dependencies/...}
   *       就能正确挂到该项目上。</li>
   * </ol>
   *
   * <h3>示例</h3>
   * <pre>{@code
   * makeContext("water", () -> {
   *   research("a", 100, () -> {            // 创建一个研究，并进入它的配置上下文
   *     contents(blockA, unitA);            // 完成后解锁的内容
   *     hideTechs();                        // 不在科技树里显示前置连线
   *   });
   *   research("b", 200, () -> {
   *     contents(blockB);
   *     dependencies("a");                  // 依赖名为 "a" 的研究（按名字引用当前区域项目）
   *     devices(researchDeviceX);           // 需要放置的研究模块
   *   });
   * });
   * }</pre>
   */
  public static class ResearchSDL{
    /**
     * 当前区域（ResearchGroup），由 makeContext 设置。
     */
    private static ResearchGroup context;
    /**
     * 暂存的“项目 → 配置回调”，待 makeContext 回放阶段逐个执行。
     */
    private static OrderedMap<ResearchProject, Runnable> tasks;
    /**
     * 当前正在配置的研究项目，由回放阶段设置。
     */
    private static ResearchProject currProject;
    /**
     * 是否正处于某项目的配置回调中（决定 contents/dependencies 等能否被调用）。
     */
    private static boolean inProjectContext;
    /**
     * 当前揭示分组栈顶；新创建的研究会继承此揭示门控。
     */
    private static RevealGroup currReveal = null;

    /**
     * 划定一个区域（zone）的研究上下文。runnable 内用 {@link #research(String, int, Runnable)} 等登记项目；
     * runnable 跑完后，DSL 会回放每个项目的配置回调，使 contents/dependencies 等正确生效。
     * 支持嵌套：内部 makeContext 结束后恢复外层 context。
     *
     * @param zoneKey  区域键，对应一个 ResearchGroup（如 {@code "water"}、{@code "boss:1"}）。
     * @param runnable 在该区域内定义研究项目的逻辑。
     */
    protected static void makeContext(String zoneKey, Runnable runnable){
      ResearchGroup last = context;
      OrderedMap<ResearchProject, Runnable> lastTasks = tasks;
      context = LuluMod.researches.makeGroup(zoneKey);
      tasks = new OrderedMap<>();

      runnable.run();
      tasks.forEach(e -> {
        inProjectContext = true;
        currProject = e.key;
        e.value.run();
        inProjectContext = false;
      });

      context = last;
      tasks = lastTasks;
    }

    /**
     * 把后续（直到 runnable 结束）创建的研究归入一个“揭示分组”（RevealGroup）。
     * 揭示分组是一种前置门控：玩家必须先揭示该组，组内研究才可见/可研究。reveal 可嵌套，
     * 内层组会以前一层组作为 {@code require}（即揭示内层前须先揭示外层）。
     *
     * @param group    要进入的揭示分组。
     * @param runnable 在该揭示分组下定义研究项目的逻辑。
     */
    protected static void reveal(RevealGroup group, Runnable runnable){
      RevealGroup last = currReveal;
      currReveal = group;
      group.require = last;
      runnable.run();
      currReveal = last;
    }

    /**
     * 按名字取当前区域下已登记的研究项目（通常用于 dependencies(String...) 之外的手动引用）。
     */
    protected static ResearchProject byName(String name){
      return context.getResearch(name);
    }

    /**
     * 创建研究项目并附带配置回调（推荐用法）：回调在回放阶段于项目上下文中执行。
     */
    protected static ResearchProject research(String name, int techRequires, int techRequiresRandom, Runnable runnable){
      ResearchProject res = research(name, techRequires, techRequiresRandom);
      res.setReveal(currReveal);
      tasks.put(res, runnable);
      return res;
    }

    /**
     * 创建研究项目并附带配置回调（推荐用法，无随机科技点）。
     */
    protected static ResearchProject research(String name, int techRequires, Runnable runnable){
      ResearchProject res = research(name, techRequires);
      res.setReveal(currReveal);
      tasks.put(res, runnable);
      return res;
    }

    /**
     * 创建研究项目（含随机科技点），仅登记、不附带配置回调。
     */
    protected static ResearchProject research(String name, int techRequires, int techRequiresRandom){
      checkContext();
      ResearchProject project = new ResearchProject(name, techRequires, techRequiresRandom);
      project.setReveal(currReveal);
      context.addProject(project);
      return project;
    }

    /**
     * 创建研究项目（无随机科技点），仅登记、不附带配置回调。
     */
    protected static ResearchProject research(String name, int techRequires){
      checkContext();
      ResearchProject project = new ResearchProject(name, techRequires);
      project.setReveal(currReveal);
      context.addProject(project);
      return project;
    }

    /**
     * 设置该研究完成后解锁的内容（建筑/单位等 UnlockableContent）。须在项目配置上下文中调用。
     */
    protected static void contents(UnlockableContent... contents){
      checkProjectContext();
      currProject.addContent(contents);
    }

    /**
     * 设置前置研究（按项目对象），全部完成才能开始本研究。须在项目配置上下文中调用。
     */
    protected static void dependencies(ResearchProject... dependencies){
      checkProjectContext();
      currProject.addDependency(dependencies);
    }

    /**
     * 设置前置研究（按名字，引用当前区域内已登记的项目）。须在项目配置上下文中调用。
     */
    protected static void dependencies(String... dependencies){
      checkProjectContext();
      for(String dependency : dependencies){
        currProject.addDependency(context.getResearch(dependency));
      }
    }

    /**
     * 设置该研究的“灵感”（Inspire），提供额外科技点或特殊激励。须在项目配置上下文中调用。
     */
    protected static void inspire(Inspire inspire){
      checkProjectContext();
      currProject.setInspire(inspire);
    }

    /**
     * 标记该研究“无视揭示限制直接显示”（show without reveal）。须在项目配置上下文中调用。
     */
    protected static void showRevealess(){
      checkProjectContext();
      currProject.showRevealess();
    }

    /**
     * 标记该研究在科技树连线中隐藏其前置科技（hide prerequisite techs）。须在项目配置上下文中调用。
     */
    protected static void hideTechs(){
      checkProjectContext();
      currProject.hideTechs();
    }

    /**
     * 设置完成本研究所需放置的研究模块（ResearchDevice）。须在项目配置上下文中调用。
     */
    protected static void devices(ResearchDevice... devices){
      checkProjectContext();
      currProject.addRequireDevice(devices);
    }

    /**
     * 设置完成本研究所需消耗的资源（ResourceStack[]），研究台将消耗这些资源以推进研究。
     * 须在项目配置上下文中调用。
     */
    protected static void cost(ResourceStack<?>... costs){
      checkProjectContext();
      currProject.researchCost = costs;
    }

    /**
     * 校验：必须在 makeContext 内、且不能在另一个项目的配置回调中。
     */
    private static void checkContext(){
      if(context == null) throw new IllegalStateException("No zone context");
      if(inProjectContext) throw new IllegalStateException("Already in project context");
    }

    /**
     * 校验：必须在某个项目的配置回调中（即 research 的 Runnable 内）。
     */
    private static void checkProjectContext(){
      if(currProject == null) throw new IllegalStateException("No project context");
      if(!inProjectContext) throw new IllegalStateException("Not in project context");
    }
  }
}
