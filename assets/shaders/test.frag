#define HIGHP

varying vec2 v_texCoords;
uniform vec2 u_resolution;
uniform sampler2D u_texture;

const int c_sampler = 64;
uniform float u_sampler;

uniform float u_intensity;
uniform float u_radius;
uniform float u_threshold;
uniform float u_softness;

float gauss(float x){
    return exp(-0.5 * x * x);
}

vec4 extractBright(vec2 uv){
    vec4 c = texture2D(u_texture, uv);
    float lum = dot(c.rgb, vec3(0.2126, 0.7152, 0.0722));
    return c * smoothstep(u_threshold, u_threshold + u_softness, lum);
}

void main(){
    vec2 uv = v_texCoords;
    vec4 base = texture2D(u_texture, uv);
    vec2 px = 1.0 / u_resolution;

    vec4 hGlow = vec4(base);
    float nor = gauss(0.0);
    for(int i = 1; i <= c_sampler; i++){
        float t1 = float(i) / float(u_sampler);
        float t2 = float(-i) / float(u_sampler);

        float w = gauss(t1 * 2.5) * step(t1, float(u_sampler));

        nor += w * 2;
        hGlow += extractBright(uv + vec2(t1 * u_radius * px.x, 0.0)) * w;
        hGlow += extractBright(uv + vec2(t2 * u_radius * px.x, 0.0)) * w;
    }
    hGlow /= nor;

    vec4 glowOut = hGlow * u_intensity;

    gl_FragColor = glowOut;
}
