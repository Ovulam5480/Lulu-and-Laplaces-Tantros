package LLL.util.struct;

import arc.func.*;
import arc.math.*;
import arc.struct.*;

import java.util.*;

public class MultiTree<T>{
  /**
   * 根节点
   */
  public TreeNode<T> root;

  /**
   * 节点总数缓存
   */
  private int nodeCount = 0;

  /**
   * 最大深度缓存
   */
  private int maxDepth = -1;

  /**
   * 随机数生成器
   */
  private static final Rand rand = new Rand();

  // ==================== 树节点内部类 ====================

  /**
   * 树节点类
   */
  public static class TreeNode<T>{
    /**
     * 节点存储的数据
     */
    public T value;

    /**
     * 父节点引用
     */
    public TreeNode<T> parent;

    /**
     * 子节点列表
     */
    public Seq<TreeNode<T>> children = new Seq<>();

    /**
     * 创建根节点
     *
     * @param value 节点值
     */
    public TreeNode(T value){
      this.value = value;
      this.parent = null;
    }

    /**
     * 创建子节点
     *
     * @param value  节点值
     * @param parent 父节点
     */
    public TreeNode(T value, TreeNode<T> parent){
      this.value = value;
      this.parent = parent;
      if(parent != null){
        parent.addChild(this);
      }
    }

    /**
     * 添加子节点
     *
     * @param child 子节点
     * @return 添加的子节点
     */
    public TreeNode<T> addChild(TreeNode<T> child){
      if(child.parent != null){
        child.parent.removeChild(child);
      }
      child.parent = this;
      children.add(child);
      return child;
    }

    /**
     * 添加多个子节点
     *
     * @param children 子节点数组
     * @return 当前节点
     */
    @SafeVarargs
    public final TreeNode<T> addChildren(TreeNode<T>... children){
      for(TreeNode<T> child : children){
        addChild(child);
      }
      return this;
    }

    /**
     * 移除子节点
     *
     * @param child 要移除的子节点
     * @return 是否成功移除
     */
    public boolean removeChild(TreeNode<T> child){
      if(children.remove(child)){
        child.parent = null;
        return true;
      }
      return false;
    }

    /**
     * 移除指定索引的子节点
     *
     * @param index 子节点索引
     * @return 被移除的节点
     */
    public TreeNode<T> removeChild(int index){
      if(index < 0 || index >= children.size){
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + children.size);
      }
      TreeNode<T> child = children.remove(index);
      child.parent = null;
      return child;
    }

    /**
     * 清空所有子节点
     *
     * @return 当前节点
     */
    public TreeNode<T> clearChildren(){
      for(TreeNode<T> child : children){
        child.parent = null;
      }
      children.clear();
      return this;
    }

    /**
     * 获取子节点数量
     *
     * @return 子节点数量
     */
    public int getChildCount(){
      return children.size;
    }

    /**
     * 获取指定索引的子节点
     *
     * @param index 索引
     * @return 子节点
     */
    public TreeNode<T> getChild(int index){
      return children.get(index);
    }

    /**
     * 查找第一个匹配条件的子节点
     *
     * @param predicate 匹配条件
     * @return 匹配的子节点，未找到返回null
     */
    public TreeNode<T> findChild(Boolf<TreeNode<T>> predicate){
      return children.find(predicate);
    }

    /**
     * 检查是否包含满足条件的子节点
     *
     * @param predicate 匹配条件
     * @return 是否包含
     */
    public boolean hasChild(Boolf<TreeNode<T>> predicate){
      return children.contains(predicate);
    }

    /**
     * 获取兄弟节点（不包括自己）
     *
     * @return 兄弟节点序列
     */
    public Seq<TreeNode<T>> getSiblings(){
      if(parent == null){
        return new Seq<>();
      }
      Seq<TreeNode<T>> siblings = parent.children.copy();
      siblings.remove(this);
      return siblings;
    }

    /**
     * 获取深度（从根节点到当前节点的路径长度）
     *
     * @return 深度
     */
    public int getDepth(){
      int depth = 0;
      TreeNode<T> current = this;
      while(current.parent != null){
        depth++;
        current = current.parent;
      }
      return depth;
    }

    /**
     * 获取树的高度（从当前节点到最远叶子节点的最长路径）
     *
     * @return 树高度
     */
    public int getHeight(){
      if(children.isEmpty()){
        return 0;
      }
      int maxHeight = 0;
      for(TreeNode<T> child : children){
        maxHeight = Math.max(maxHeight, child.getHeight());
      }
      return maxHeight + 1;
    }

    /**
     * 检查是否为叶子节点
     *
     * @return 是否为叶子节点
     */
    public boolean isLeaf(){
      return children.isEmpty();
    }

    /**
     * 检查是否为根节点
     *
     * @return 是否为根节点
     */
    public boolean isRoot(){
      return parent == null;
    }

    /**
     * 检查是否为某个节点的祖先
     *
     * @param node 目标节点
     * @return 是否为祖先
     */
    public boolean isAncestorOf(TreeNode<T> node){
      TreeNode<T> current = node.parent;
      while(current != null){
        if(current == this){
          return true;
        }
        current = current.parent;
      }
      return false;
    }

    /**
     * 检查是否为某个节点的后代
     *
     * @param node 目标节点
     * @return 是否为后代
     */
    public boolean isDescendantOf(TreeNode<T> node){
      return node.isAncestorOf(this);
    }

    /**
     * 前序遍历（根-左-右）
     *
     * @param consumer 处理函数
     */
    public void traversePreOrder(Cons<TreeNode<T>> consumer){
      consumer.get(this);
      for(TreeNode<T> child : children){
        child.traversePreOrder(consumer);
      }
    }

    /**
     * 后序遍历（左-右-根）
     *
     * @param consumer 处理函数
     */
    public void traversePostOrder(Cons<TreeNode<T>> consumer){
      for(TreeNode<T> child : children){
        child.traversePostOrder(consumer);
      }
      consumer.get(this);
    }

    /**
     * 层序遍历（广度优先）
     *
     * @param consumer 处理函数
     */
    public void traverseLevelOrder(Cons<TreeNode<T>> consumer){
      Seq<TreeNode<T>> queue = new Seq<>();
      queue.add(this);

      while(!queue.isEmpty()){
        TreeNode<T> current = queue.remove(0);
        consumer.get(current);
        queue.addAll(current.children);
      }
    }

    /**
     * 查找满足条件的第一个节点（深度优先搜索）
     *
     * @param predicate 匹配条件
     * @return 匹配的节点，未找到返回null
     */
    public TreeNode<T> findFirst(Boolf<TreeNode<T>> predicate){
      if(predicate.get(this)){
        return this;
      }
      for(TreeNode<T> child : children){
        TreeNode<T> result = child.findFirst(predicate);
        if(result != null){
          return result;
        }
      }
      return null;
    }

    /**
     * 查找所有满足条件的节点
     *
     * @param predicate 匹配条件
     * @return 匹配的节点列表
     */
    public Seq<TreeNode<T>> findAll(Boolf<TreeNode<T>> predicate){
      Seq<TreeNode<T>> result = new Seq<>();
      traversePreOrder(node -> {
        if(predicate.get(node)){
          result.add(node);
        }
      });
      return result;
    }

    /**
     * 获取从根节点到当前节点的路径
     *
     * @return 路径节点列表
     */
    public Seq<TreeNode<T>> getPathFromRoot(){
      Seq<TreeNode<T>> path = new Seq<>();
      TreeNode<T> current = this;
      while(current != null){
        path.add(current);
        current = current.parent;
      }
      path.reverse();
      return path;
    }

    /**
     * 克隆整棵树
     *
     * @param copier 数据复制函数
     * @return 克隆的树根节点
     */
    public TreeNode<T> clone(Func<T, T> copier){
      TreeNode<T> cloned = new TreeNode<>(copier.get(value));

      for(TreeNode<T> child : children){
        cloned.addChild(child.clone(copier));
      }

      return cloned;
    }

    /**
     * 转换为字符串表示
     *
     * @return 字符串表示
     */
    @Override
    public String toString(){
      return "TreeNode{" +
        "value=" + value +
        ", children=" + children.size +
        '}';
    }

    /**
     * 获取树的可视化字符串（用于调试）
     *
     * @return 可视化字符串
     */
    public String toVisualString(){
      StringBuilder sb = new StringBuilder();
      toVisualString(sb, "", true);
      return sb.toString();
    }

    private void toVisualString(StringBuilder sb, String prefix, boolean isLast){
      sb.append(prefix);
      sb.append(isLast ? "└── " : "├── ");
      sb.append(value.toString()).append("\n");

      String childPrefix = prefix + (isLast ? "    " : "│   ");

      for(int i = 0; i < children.size; i++){
        children.get(i).toVisualString(sb, childPrefix, i == children.size - 1);
      }
    }
  }

  // ==================== 遍历策略枚举 ====================

  /**
   * 遍历策略枚举
   */
  public enum TraversalStrategy{
    /**
     * 前序遍历
     */
    PRE_ORDER,
    /**
     * 后序遍历
     */
    POST_ORDER,
    /**
     * 层序遍历
     */
    LEVEL_ORDER,
    /**
     * 深度优先搜索
     */
    DFS,
    /**
     * 广度优先搜索
     */
    BFS
  }

  // ==================== 树管理器方法 ====================

  /**
   * 创建空树
   */
  public MultiTree(){
    this.root = null;
  }

  /**
   * 创建带根节点的树
   *
   * @param rootValue 根节点值
   */
  public MultiTree(T rootValue){
    this.root = new TreeNode<>(rootValue);
    this.nodeCount = 1;
    this.maxDepth = 0;
  }

  /**
   * 设置根节点
   *
   * @param root 新的根节点
   * @return 当前树
   */
  public MultiTree<T> setRoot(TreeNode<T> root){
    this.root = root;
    invalidateCache();
    recalculateStats();
    return this;
  }

  /**
   * 创建并设置新的根节点
   *
   * @param value 根节点值
   * @return 新的根节点
   */
  public TreeNode<T> createRoot(T value){
    this.root = new TreeNode<>(value);
    this.nodeCount = 1;
    this.maxDepth = 0;
    return this.root;
  }

  /**
   * 获取节点总数
   *
   * @return 节点总数
   */
  public int getNodeCount(){
    if(nodeCount == 0 && root != null){
      recalculateStats();
    }
    return nodeCount;
  }

  /**
   * 获取树的最大深度
   *
   * @return 最大深度
   */
  public int getMaxDepth(){
    if(maxDepth == -1 && root != null){
      recalculateStats();
    }
    return maxDepth;
  }

  /**
   * 检查树是否为空
   *
   * @return 是否为空
   */
  public boolean isEmpty(){
    return root == null;
  }

  /**
   * 清空树
   *
   * @return 当前树
   */
  public MultiTree<T> clear(){
    this.root = null;
    this.nodeCount = 0;
    this.maxDepth = -1;
    return this;
  }

  /**
   * 前序遍历整棵树
   *
   * @param consumer 处理函数
   */
  public void traversePreOrder(Cons<TreeNode<T>> consumer){
    if(root != null){
      root.traversePreOrder(consumer);
    }
  }

  /**
   * 后序遍历整棵树
   *
   * @param consumer 处理函数
   */
  public void traversePostOrder(Cons<TreeNode<T>> consumer){
    if(root != null){
      root.traversePostOrder(consumer);
    }
  }

  /**
   * 层序遍历整棵树
   *
   * @param consumer 处理函数
   */
  public void traverseLevelOrder(Cons<TreeNode<T>> consumer){
    if(root != null){
      root.traverseLevelOrder(consumer);
    }
  }

  /**
   * 查找满足条件的第一个节点
   *
   * @param predicate 匹配条件
   * @return 匹配的节点
   */
  public TreeNode<T> findFirst(Boolf<TreeNode<T>> predicate){
    return root != null ? root.findFirst(predicate) : null;
  }

  /**
   * 查找所有满足条件的节点
   *
   * @param predicate 匹配条件
   * @return 匹配的节点列表
   */
  public Seq<TreeNode<T>> findAll(Boolf<TreeNode<T>> predicate){
    Seq<TreeNode<T>> result = new Seq<>();
    traversePreOrder(node -> {
      if(predicate.get(node)){
        result.add(node);
      }
    });
    return result;
  }

  /**
   * 获取所有叶子节点
   *
   * @return 叶子节点列表
   */
  public Seq<TreeNode<T>> getLeaves(){
    return findAll(TreeNode::isLeaf);
  }

  /**
   * 获取指定深度的所有节点
   *
   * @param depth 目标深度
   * @return 指定深度的节点列表
   */
  public Seq<TreeNode<T>> getNodesAtDepth(int depth){
    Seq<TreeNode<T>> result = new Seq<>();
    traversePreOrder(node -> {
      if(node.getDepth() == depth){
        result.add(node);
      }
    });
    return result;
  }

  /**
   * 获取随机节点
   *
   * @param seed 随机种子
   * @return 随机节点
   */
  public TreeNode<T> getRandomNode(long seed){
    if(root == null) return null;

    rand.setSeed(seed);
    int targetIndex = rand.nextInt(getNodeCount());
    final int[] currentIndex = {0};

    return findFirst(node -> currentIndex[0]++ == targetIndex);
  }

  /**
   * 计算树的平衡因子（最大深度与最小深度的比值）
   *
   * @return 平衡因子
   */
  public float getBalanceFactor(){
    if(root == null) return 0f;

    int minDepth = findMinDepth();
    int maxDepth = getMaxDepth();

    return minDepth == 0 ? Float.POSITIVE_INFINITY : (float)maxDepth / minDepth;
  }

  /**
   * 查找最小深度
   *
   * @return 最小深度
   */
  private int findMinDepth(){
    if(root == null) return 0;

    Seq<TreeNode<T>> leaves = getLeaves();
    int minDepth = Integer.MAX_VALUE;
    for(TreeNode<T> leaf : leaves){
      minDepth = Math.min(minDepth, leaf.getDepth());
    }
    return minDepth;
  }

  /**
   * 将树转换为数组表示（层序）
   *
   * @return 节点值数组
   */
  public Seq<T> toArray(){
    Seq<T> result = new Seq<>();
    traverseLevelOrder(node -> result.add(node.value));
    return result;
  }

  /**
   * 从数组构建完全多叉树
   *
   * @param values          节点值数组
   * @param branchingFactor 分支因子（每个节点的最大子节点数）
   * @return 构建的树
   */
  public static <V> MultiTree<V> fromArray(Seq<V> values, int branchingFactor){
    if(values.isEmpty()){
      return new MultiTree<>();
    }

    MultiTree<V> tree = new MultiTree<>();
    TreeNode<V> rootNode = new TreeNode<>(values.first());
    tree.root = rootNode;

    Seq<TreeNode<V>> queue = new Seq<>();
    queue.add(rootNode);

    int index = 1;
    while(index < values.size && !queue.isEmpty()){
      TreeNode<V> current = queue.remove(0);

      for(int i = 0; i < branchingFactor && index < values.size; i++){
        TreeNode<V> child = new TreeNode<>(values.get(index++), current);
        queue.add(child);
      }
    }

    tree.recalculateStats();
    return tree;
  }

  /**
   * 克隆整棵树
   *
   * @param copier 数据复制函数
   * @return 克隆的树
   */
  public MultiTree<T> clone(Func<T, T> copier){
    MultiTree<T> cloned = new MultiTree<>();
    if(root != null){
      cloned.root = root.clone(copier);
      cloned.recalculateStats();
    }
    return cloned;
  }

  /**
   * 合并另一棵树作为子树
   *
   * @param subtree         要合并的子树
   * @param parentPredicate 父节点选择条件
   * @return 是否合并成功
   */
  public boolean mergeSubtree(MultiTree<T> subtree, Boolf<TreeNode<T>> parentPredicate){
    if(subtree.root == null) return false;
    if(root == null){
      this.root = subtree.root;
      recalculateStats();
      return true;
    }

    TreeNode<T> parent = findFirst(parentPredicate);
    if(parent != null){
      parent.addChild(subtree.root);
      nodeCount += subtree.getNodeCount();
      maxDepth = Math.max(maxDepth, parent.getDepth() + subtree.getMaxDepth() + 1);
      return true;
    }

    return false;
  }

  /**
   * 剪枝操作：移除满足条件的子树
   *
   * @param predicate 剪枝条件
   * @return 被移除的节点数量
   */
  public int prune(Boolf<TreeNode<T>> predicate){
    if(root == null) return 0;

    int removedCount = 0;
    Seq<TreeNode<T>> toRemove = new Seq<>();

    traversePreOrder(node -> {
      if(predicate.get(node) && node.parent != null){
        toRemove.add(node);
      }
    });

    for(TreeNode<T> node : toRemove){
      removedCount += countSubtreeNodes(node);
      node.parent.removeChild(node);
    }

    if(!toRemove.isEmpty()){
      recalculateStats();
    }

    return removedCount;
  }

  /**
   * 统计子树节点数
   *
   * @param node 子树根节点
   * @return 节点数
   */
  private int countSubtreeNodes(TreeNode<T> node){
    int count = 1;
    for(TreeNode<T> child : node.children){
      count += countSubtreeNodes(child);
    }
    return count;
  }

  /**
   * 重新计算统计信息
   */
  private void recalculateStats(){
    if(root == null){
      nodeCount = 0;
      maxDepth = -1;
      return;
    }

    nodeCount = 0;
    maxDepth = 0;

    traversePreOrder(node -> {
      nodeCount++;
      maxDepth = Math.max(maxDepth, node.getDepth());
    });
  }

  /**
   * 使缓存失效
   */
  private void invalidateCache(){
    nodeCount = 0;
    maxDepth = -1;
  }

  /**
   * 获取树的可视化字符串
   *
   * @return 可视化字符串
   */
  public String toVisualString(){
    return root != null ? root.toVisualString() : "Empty Tree";
  }

  /**
   * 转换为字符串表示
   *
   * @return 字符串表示
   */
  @Override
  public String toString(){
    return "MultiTree{" +
      "nodes=" + getNodeCount() +
      ", depth=" + getMaxDepth() +
      ", root=" + (root != null ? root.value : "null") +
      '}';
  }

  // ==================== 迭代器相关方法 ====================

  /**
   * 创建前序遍历迭代器
   *
   * @return 迭代器
   */
  public TreeIterator<T> preOrderIterator(){
    return new TreeIterator<>(root, TraversalStrategy.PRE_ORDER);
  }

  /**
   * 创建后序遍历迭代器
   *
   * @return 迭代器
   */
  public TreeIterator<T> postOrderIterator(){
    return new TreeIterator<>(root, TraversalStrategy.POST_ORDER);
  }

  /**
   * 创建层序遍历迭代器
   *
   * @return 迭代器
   */
  public TreeIterator<T> levelOrderIterator(){
    return new TreeIterator<>(root, TraversalStrategy.LEVEL_ORDER);
  }

  /**
   * 创建DFS迭代器
   *
   * @return 迭代器
   */
  public TreeIterator<T> dfsIterator(){
    return new TreeIterator<>(root, TraversalStrategy.DFS);
  }

  /**
   * 创建BFS迭代器
   *
   * @return 迭代器
   */
  public TreeIterator<T> bfsIterator(){
    return new TreeIterator<>(root, TraversalStrategy.BFS);
  }

  // ==================== 树迭代器内部类 ====================

  /**
   * 树迭代器类
   */
  public static class TreeIterator<T> implements Iterator<TreeNode<T>>, Iterable<TreeNode<T>>{
    private final TreeNode<T> root;
    private final TraversalStrategy strategy;
    private final Seq<TreeNode<T>> stack;
    private final ObjectSet<TreeNode<T>> visited;
    private boolean hasNextCached = false;
    private TreeNode<T> nextNode = null;

    public TreeIterator(TreeNode<T> root, TraversalStrategy strategy){
      this.root = root;
      this.strategy = strategy;
      this.stack = new Seq<>();
      this.visited = new ObjectSet<>();

      if(root != null){
        initialize();
      }
    }

    private void initialize(){
      switch(strategy){
        case PRE_ORDER:
        case DFS:
          stack.add(root);
          break;
        case POST_ORDER:
          initializePostOrder();
          break;
        case LEVEL_ORDER:
        case BFS:
          stack.add(root);
          break;
      }
      computeNext();
    }

    private void initializePostOrder(){
      Seq<TreeNode<T>> tempStack = new Seq<>();
      tempStack.add(root);

      while(!tempStack.isEmpty()){
        TreeNode<T> node = tempStack.pop();
        stack.add(node);

        for(int i = 0; i < node.children.size; i++){
          tempStack.add(node.children.get(i));
        }
      }
    }

    @Override
    public boolean hasNext(){
      return hasNextCached;
    }

    @Override
    public TreeNode<T> next(){
      if(!hasNextCached){
        throw new NoSuchElementException();
      }

      TreeNode<T> result = nextNode;
      computeNext();
      return result;
    }

    private void computeNext(){
      nextNode = null;
      hasNextCached = false;

      switch(strategy){
        case PRE_ORDER:
        case DFS:
          nextNode = getNextPreOrder();
          break;
        case POST_ORDER:
          nextNode = getNextPostOrder();
          break;
        case LEVEL_ORDER:
        case BFS:
          nextNode = getNextLevelOrder();
          break;
      }

      if(nextNode != null){
        hasNextCached = true;
      }
    }

    private TreeNode<T> getNextPreOrder(){
      if(stack.isEmpty()) return null;

      TreeNode<T> node = stack.pop();
      visited.add(node);

      for(int i = node.children.size - 1; i >= 0; i--){
        TreeNode<T> child = node.children.get(i);
        if(!visited.contains(child)){
          stack.add(child);
        }
      }

      return node;
    }

    private TreeNode<T> getNextPostOrder(){
      if(stack.isEmpty()) return null;
      return stack.pop();
    }

    private TreeNode<T> getNextLevelOrder(){
      if(stack.isEmpty()) return null;

      TreeNode<T> node = stack.remove(0);
      visited.add(node);

      for(TreeNode<T> child : node.children){
        if(!visited.contains(child)){
          stack.add(child);
          visited.add(child);
        }
      }

      return node;
    }

    @Override
    public Iterator<TreeNode<T>> iterator(){
      return this;
    }

    /**
     * 过滤节点
     *
     * @param predicate 过滤条件
     * @return 过滤后的迭代器
     */
    public TreeIterator<T> filter(Boolf<TreeNode<T>> predicate){
      return new FilteringIterator<>(this, predicate);
    }

    /**
     * 映射节点值
     *
     * @param mapper 映射函数
     * @param <R>    目标类型
     * @return 映射后的迭代器
     */
    public <R> MappingIterator<T, R> map(Func<TreeNode<T>, R> mapper){
      return new MappingIterator<>(this, mapper);
    }

    /**
     * 收集所有节点到序列
     *
     * @return 节点序列
     */
    public Seq<TreeNode<T>> toSeq(){
      Seq<TreeNode<T>> result = new Seq<>();
      while(hasNext()){
        result.add(next());
      }
      return result;
    }

    /**
     * 收集所有节点值到序列
     *
     * @return 值序列
     */
    public Seq<T> toValueSeq(){
      Seq<T> result = new Seq<>();
      while(hasNext()){
        result.add(next().value);
      }
      return result;
    }

    // 过滤迭代器实现
    private static class FilteringIterator<T> extends TreeIterator<T>{
      private final TreeIterator<T> source;
      private final Boolf<TreeNode<T>> predicate;
      private TreeNode<T> nextNode;

      public FilteringIterator(TreeIterator<T> source, Boolf<TreeNode<T>> predicate){
        super(source.root, source.strategy);
        this.source = source;
        this.predicate = predicate;
      }

      @Override
      public boolean hasNext(){
        while(source.hasNext()){
          TreeNode<T> next = source.next();
          if(predicate.get(next)){
            this.nextNode = next;
            return true;
          }
        }
        return false;
      }

      @Override
      public TreeNode<T> next(){
        if(nextNode == null && !hasNext()){
          throw new NoSuchElementException();
        }
        TreeNode<T> result = nextNode;
        nextNode = null;
        return result;
      }
    }

    // 映射迭代器实现
    public static class MappingIterator<T, R> implements Iterator<R>, Iterable<R>{
      private final TreeIterator<T> source;
      private final Func<TreeNode<T>, R> mapper;

      public MappingIterator(TreeIterator<T> source, Func<TreeNode<T>, R> mapper){
        this.source = source;
        this.mapper = mapper;
      }

      @Override
      public boolean hasNext(){
        return source.hasNext();
      }

      @Override
      public R next(){
        return mapper.get(source.next());
      }

      @Override
      public Iterator<R> iterator(){
        return this;
      }

      /**
       * 收集所有映射结果到序列
       *
       * @return 结果序列
       */
      public Seq<R> toSeq(){
        Seq<R> result = new Seq<>();
        while(hasNext()){
          result.add(next());
        }
        return result;
      }
    }
  }
}