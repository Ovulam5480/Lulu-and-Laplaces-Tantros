package LLL.world.particles.models;

import LLL.world.particles.*;

public class DrawDefaultTrailParticle extends ParticleModel{
  @Override
  public void drawTrail(Particle particle){
    float n = 0;
    for(Particle.Cloud c : particle){
      c.draw(1 - n / particle.cloudCount(), 1 - (n + 1) / particle.cloudCount());
      n++;
    }
  }
}
