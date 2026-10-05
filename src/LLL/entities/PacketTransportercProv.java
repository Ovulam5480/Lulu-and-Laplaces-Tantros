package LLL.entities;


import LLL.entities.gen.*;

public interface PacketTransportercProv{
  PacketTransporterc get();

  //Blocks.impactReactor
}

//😡
//@Annotations.EntityDef(TestAc.class)
//@Annotations.EntityComponent()
//public class TestAComp {
//    public boolean boolMethodA(TestA test){
//        return true;
//    }
//gen fail
//    public boolean boolMethodAc(TestAc test){
//        return true;
//    }
//gen fail
//    public boolean boolMethodB(TestBc test){
//        return true;
//    }
//    public boolean boolMethodC(TestC test){
//        Log.info(test.testAc);
//        return true;
//    }
//    public boolean boolMethodD(){
//        return true;
//    }
//    public boolean boolMethodE(Entityc test){
//        return true;
//    }
//
//    public class TestC{
//        public TestAc testAc;
//
//        IFX ifx = () -> testAc;
//    }
//}
//@Annotations.EntityComponent()
//class TestBComp{}
//
//interface IFX{
//    TestAc get();
//}
