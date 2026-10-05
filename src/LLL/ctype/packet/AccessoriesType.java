package LLL.ctype.packet;

public enum AccessoriesType{
  plate("plate"),           // 板
  screw("screw"),           // 螺丝
  gear("gear"),             // 齿轮
  rod("rod"),               // 杆
  beam("beam"),             // 梁
  shaft("shaft"),           // 轴

  nut("nut"),               // 螺母
  bolt("bolt"),             // 螺栓
  washer("washer"),         // 垫圈
  pin("pin"),               // 销
  rivet("rivet"),           // 铆钉

  belt("belt"),             // 皮带
  chain("chain"),           // 链条
  pulley("pulley"),         // 滑轮
  bearing("bearing"),       // 轴承
  coupling("coupling"),     // 联轴器

  gasket("gasket"),         // 垫片
  seal("seal"),             // 密封圈
  guard("guard"),           // 防护罩

  handle("handle"),         // 手柄
  lever("lever"),           // 杠杆
  spring("spring");         // 弹簧

  private final String typeName;
  public static AccessoriesType[] all = values();

  AccessoriesType(String typeName){
    this.typeName = typeName;
  }

  public String getTypeName(){
    return typeName;
  }
}