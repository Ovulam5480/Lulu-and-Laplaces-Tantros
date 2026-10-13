#define HIGHP

varying vec2 v_texCoords;
uniform vec2 u_resolution;
uniform sampler2D u_texture;

uniform float u_coe1, u_coe2;
uniform int u_bound;
uniform float u_normalization;

float normal(float x){
    return u_coe1 * exp(x * x * u_coe2);
}

void main(){
    vec2 uv = v_texCoords;

    float stepX = 1.0 / u_resolution.x;
    vec4 color = texture2D(u_texture, uv) * normal(0.0) * u_normalization;

    for(int i = 1; i <= u_bound; i++){
        float w = normal(float(i)) * u_normalization;
        float off = float(i) * stepX;
        color += texture2D(u_texture, vec2(uv.x + off, uv.y)) * w;
        color += texture2D(u_texture, vec2(uv.x - off, uv.y)) * w;
    }

    gl_FragColor = color;
}
