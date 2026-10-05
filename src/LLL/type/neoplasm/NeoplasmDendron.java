package LLL.type.neoplasm;

import LLL.util.struct.*;
import LLL.world.neoplasm.effect.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

public class NeoplasmDendron{
  public NeoplasmNeuron.NeoplasmNeuronBuild neuron;
  public MultiTree<Position> tree;
  public Seq<MultiTree.TreeNode<Position>> activityNodes = new Seq<>();
  public Func2<Float, Float, Position> constructor;

  public NeoplasmDendron(NeoplasmNeuron.NeoplasmNeuronBuild neuron, Func2<Float, Float, Position> constructor){
    this.neuron = neuron;
    this.constructor = constructor;

    tree = new MultiTree<>(neuron);
    activityNodes.add(tree.root);
  }

  public void update(){
    MultiTree.TreeNode<Position> node = activityNodes.random();

    Vec2 target = Tmp.v1;

    float neuronX = neuron.x;
    float neuronY = neuron.y;
    Team neuronTeam = neuron.team;

    if(Vars.state.teams.closestEnemyCore(neuronX, neuronY, neuronTeam) != null){
      target.set(Vars.state.teams.closestEnemyCore(neuronX, neuronY, neuronTeam));
    }else if(Vars.state.rules.waves && neuronTeam == state.rules.defaultTeam
      && Geometry.findClosest(neuronX, neuronY, Vars.spawner.getSpawns()) != null){
      target.set(Geometry.findClosest(neuronX, neuronY, Vars.spawner.getSpawns())).scl(8);
    }else{
      target.set(neuron).sub(node.value).rotate(91f).setLength(50f).add(node.value);
    }

    float parentAngle = getParentsAngle(node);

    float randAngle = Mathf.range(75f);
    float distance = Mathf.random(20f, 40f);

    float angle = Angles.moveToward(parentAngle + randAngle, node.value.angleTo(target) + Mathf.range(5f), 20);

    Vec2 newNodePos = new Vec2().trns(angle, distance).add(node.value);

    if(contain(newNodePos.x, newNodePos.y)){
      MultiTree.TreeNode<Position> child = new MultiTree.TreeNode<>(constructor.get(newNodePos.x, newNodePos.y), node);
      node.addChild(child);

      activityNodes.add(child);
      if(activityNodes.size > 20){
        activityNodes.remove(0);
      }
    }
  }

  //todo 用于调试
  public void draw(){
    tree.traversePreOrder(tn -> {
      Draw.color(Color.red);
      Fill.circle(tn.value.getX(), tn.value.getY(), 4f);
      if(tn.isRoot()) return;

      Lines.stroke(2f, Pal.neoplasm2);
      Lines.line(tn.value.getX(), tn.value.getY(), tn.parent.value.getX(), tn.parent.value.getY());
    });
  }

  public static boolean contain(float x, float y){
    return x >= 0 && x < Vars.world.width() * 8 && y >= 0 && y < Vars.world.height() * 8;
  }

  public float getParentsAngle(MultiTree.TreeNode<Position> currentNode){
    if(currentNode.isRoot()){
      return Mathf.random(360f);
    }

    Position current = currentNode.value;
    Position parent = currentNode.parent.value;

    float mainDirection = parent.angleTo(current);

    return mainDirection;
  }
}
