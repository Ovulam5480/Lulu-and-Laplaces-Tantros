package LLL.entities.neoplasmBehavior;

import LLL.content.*;
import LLL.content.blocks.*;
import LLL.entities.neoplasmBehavior.neoplasmGrow.*;
import LLL.lib.gdxAI.btree.*;
import LLL.lib.gdxAI.btree.branch.*;
import arc.func.*;
import arc.struct.*;
import mindustry.content.*;

public class NeoplasmTrees{
  public static NeoplasmTree createPorifera(){
    return buildTree(builder -> {
      builder.root(new Parallel<>(Parallel.Policy.SelectorAllExecuted, Parallel.Orchestrator.Resume, new Seq<>()))
        .branch(new Selector<>(), neoplasm -> {
          neoplasm.add(NeoplasmGrows.growBranchialHeart(), 240f);
        })

        .branch(new Sequence<>(), ganglion -> {
          ganglion.add(NeoplasmGrows.growGanglion(2), b -> (0.99f - b.getItemFract(OvulamItems.phosphorus)) * 1800f + 240f);
          ganglion.add(NeoplasmGrows.growGanglion(3), b -> (0.99f - b.getItemFract(OvulamItems.phosphorus)) * 1500f + 200f);
          ganglion.add(NeoplasmGrows.growGanglion(4), b -> (0.99f - b.getItemFract(OvulamItems.phosphorus)) * 1200f + 160f);
          ganglion.add(NeoplasmGrows.growSyncytium(), 60 * 60 * 2);
        })

        .branch(new Selector<>(), trophosome -> {
          trophosome.add(NeoplasmGrows.growPhosphorusLarvaCyst(), 2400f);
        })

        .branch(new Parallel<>(Parallel.Policy.SelectorAllExecuted, Parallel.Orchestrator.Resume, new Seq<>()), defense -> {
          defense.add(NeoplasmGrows.growDodecactinBase(), b -> 3600f * (3 - 2.5f *
              Math.min(b.getItemFract(Items.silicon),
                b.getItemFract(OvulamItems.ferrum))))
            .add(NeoplasmGrows.growHexactinBase(), b -> 3000f * (3 - 2.5f * b.getItemFract(Items.silicon)))
            .add(NeoplasmGrows.growTriaxonBase(), 3000f)
            .add(NeoplasmGrows.growSettledlarvaCyst(), b -> 9600f * (3 - 2.5f *
              (Math.min(b.getOwnerBlock(Neoplasm.neoplasmHexactinBase) / 3f,
                b.getOwnerBlock(Neoplasm.neoplasmTriaxonBase) / 5f) -
                b.getOwnerBlock(Neoplasm.settledlarvaCyst))));
        })

        .branch(new Selector<>(), confront -> {
          confront.add(NeoplasmGrows.growMurex(), 20f);
        })

        .branch(new Selector<>(), floorOre -> {
          floorOre.add(NeoplasmGrows.growRadula());
        })

        .branch(new Selector<>(), wallOre -> {
          wallOre.add(NeoplasmGrows.growPholas());
        })

        .branch(new Selector<>(), processing -> {
          processing.add(NeoplasmGrows.growFerrumPylorus(), 120f)
            .add(NeoplasmGrows.growSiliconPylorus(), 120f)
            .add(NeoplasmGrows.growPhosphorusPylorus(), 120f)
            .add(NeoplasmGrows.growCalciumPylorus(), 120f);
        })

        .branch(new Selector<>(), arterius -> {
          arterius.add(NeoplasmGrows.growArterius(), b -> (0.99f - Math.min(b.getItemFract(OvulamItems.calcium), b.getItemFract(OvulamItems.phosphorus))) * 240);
        })

        .branch(new Selector<>(), aortus -> {
          aortus.add(NeoplasmGrows.growAortus(), b -> (0.99f - b.getItemFract(OvulamItems.ferrum)) * 420);
        });
    });
  }

  /**
   * 使用Builder构建行为树
   */
  public static NeoplasmTree buildTree(Cons<TreeBuilder> config){
    TreeBuilder builder = new TreeBuilder();
    config.get(builder);
    return builder.build();
  }

  /**
   * 行为树包装类
   */
  public static class NeoplasmTree{
    public BehaviorTree<NeoplasmBehavior> tree;
    public Seq<GrowLeafTask> allTasks;

    NeoplasmTree(BehaviorTree<NeoplasmBehavior> tree, Seq<GrowLeafTask> allTasks){
      this.tree = tree;
      this.allTasks = allTasks;
    }
  }

  /**
   * 行为树构建器 - 支持链式调用和层级嵌套
   */
  public static class TreeBuilder{
    private Task<NeoplasmBehavior> rootTask;
    private final Seq<Task<NeoplasmBehavior>> contextStack = new Seq<>();
    private final Seq<GrowLeafTask> collectedTasks = new Seq<>();

    /**
     * 设置根节点
     */
    public TreeBuilder root(Task<NeoplasmBehavior> root){
      if(this.rootTask != null){
        throw new IllegalStateException("Root task already set");
      }
      this.rootTask = root;
      this.contextStack.add(root);
      return this;
    }

    /**
     * 添加叶子任务到当前分支
     */
    public TreeBuilder add(GrowLeafTask task){
      ensureContext();
      Task<NeoplasmBehavior> parent = contextStack.peek();

      if(!(parent instanceof BranchTask)){
        throw new IllegalStateException("Parent is not a BranchTask: " + parent.getClass().getSimpleName());
      }

      parent.addChild(task);
      collectedTasks.add(task);
      return this;
    }

    public TreeBuilder add(GrowLeafTask task, float interval){
      ensureContext();
      Task<NeoplasmBehavior> parent = contextStack.peek();

      if(!(parent instanceof BranchTask)){
        throw new IllegalStateException("Parent is not a BranchTask: " + parent.getClass().getSimpleName());
      }

      task.setInterval(interval);
      parent.addChild(task);
      collectedTasks.add(task);
      return this;
    }

    public TreeBuilder add(GrowLeafTask task, Func<NeoplasmBehavior, Float> interval){
      ensureContext();
      Task<NeoplasmBehavior> parent = contextStack.peek();

      if(!(parent instanceof BranchTask)){
        throw new IllegalStateException("Parent is not a BranchTask: " + parent.getClass().getSimpleName());
      }

      task.setInterval(interval);
      parent.addChild(task);
      collectedTasks.add(task);
      return this;
    }

    /**
     * 创建子分支并配置
     *
     * @param branch 分支节点（Selector/Sequence等）
     * @param config 分支配置回调
     */
    public TreeBuilder branch(BranchTask<NeoplasmBehavior> branch, Cons<TreeBuilder> config){
      ensureContext();
      Task<NeoplasmBehavior> parent = contextStack.peek();

      if(!(parent instanceof BranchTask)){
        throw new IllegalStateException("Parent is not a BranchTask: " + parent.getClass().getSimpleName());
      }

      // 将分支添加到父节点
      parent.addChild(branch);

      // 进入新分支上下文
      contextStack.add(branch);
      config.get(this);
      contextStack.pop();

      return this;
    }

    /**
     * 构建最终的行为树
     */
    NeoplasmTree build(){
      if(rootTask == null){
        throw new IllegalStateException("Root task not set. Call root() first.");
      }

      if(contextStack.size != 1){
        throw new IllegalStateException("Unbalanced branch nesting. Check your branch() calls.");
      }

      BehaviorTree<NeoplasmBehavior> tree = new BehaviorTree<>(rootTask);
      return new NeoplasmTree(tree, collectedTasks);
    }

    private void ensureContext(){
      if(contextStack.isEmpty()){
        throw new IllegalStateException("No context available. Call root() first.");
      }
    }
  }
}
