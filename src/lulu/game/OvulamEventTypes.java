package lulu.game;

import lulu.research.*;

public class OvulamEventTypes{
  public static class ResearchCompletedEvent{
    public final ResearchProject research;

    public ResearchCompletedEvent(ResearchProject research){
      this.research = research;
    }
  }

  public static class ResearchInspiredEvent{
    public final Inspire inspire;
    public final ResearchProject research;

    public ResearchInspiredEvent(Inspire inspire, ResearchProject research){
      this.inspire = inspire;
      this.research = research;
    }
  }

  public static class RevealedEvent{
    public final RevealGroup group;

    public RevealedEvent(RevealGroup group){
      this.group = group;
    }
  }
}
