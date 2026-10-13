#define HIGHP

#define maxCount 1024

varying vec2 v_texCoords;
uniform vec2 u_resolution;
uniform sampler2D u_texture;

uniform float metaBallX[maxCount];
uniform float metaBallY[maxCount];
uniform float metaBallRadius[maxCount];
uniform float metaBallColor[maxCount];

uniform int metaBallCount;

void main(){
    vec2 c = v_texCoords.xy;
    vec4 color = texture2D(u_texture, c);

    for(int i = 0; i < metaBallCount; i++){
        vec2 pos = vec2(metaBallX[i], metaBallY[i]);
        vec2 a = c * u_resolution;

        float radius = metaBallRadius[i];
        float distance = distance(a, pos);

        if(distance < radius){
            int rgba8888 = int(metaBallColor[i]);

            vec4 ballColor = vec4(
            float((rgba8888 >> 24) & 0xFF) / 255.0,
            float((rgba8888 >> 16) & 0xFF) / 255.0,
            float((rgba8888 >> 8) & 0xFF) / 255.0,
            float((rgba8888) & 0xFF) / 255.0);

            gl_FragColor = ballColor;
            return;
        }
    }

    gl_FragColor = vec4(1.0, 0.5, 0.0, 0.2);
}