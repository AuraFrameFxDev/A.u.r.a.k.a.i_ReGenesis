# 🔮 WebGL to AGSL Shader Techniques Guide

**Purpose:** Map WebGL/Three.js/ShaderToy concepts to Android AGSL for high-performance particle rendering.

---

## 1. Architecture Overview

```
WebGL/Three.js Pipeline          AGSL/Android Pipeline
─────────────────────          ─────────────────────
Vertex Shader        →          N/A (Compose handles)
Fragment Shader      →          RuntimeShader (AGSL)
Uniform Buffers      →          setFloatUniform()
Texture Sampling     →          shader.eval()
Frame Buffer         →          Canvas.drawRect(shader)
```

---

## 2. Core Syntax Mapping

| WebGL/GLSL | AGSL | Notes |
|------------|------|-------|
| `vec4` | `half4` or `float4` | AGSL prefers half for color |
| `vec3` | `half3` or `float3` | Use half for RGB |
| `vec2` | `half2` or `float2` | UV coordinates |
| `float` | `float` | Scalar values |
| `int` | `int` | Loop counters |
| `gl_FragCoord` | `fragCoord` (param) | Pixel position |
| `gl_FragColor` | `return half4` | Output color |
| `uniform vec2 u_res` | `uniform float2 iResolution` | Screen size |
| `uniform float u_time` | `uniform float iTime` | Time in seconds |
| `texture2D(sampler, uv)` | `shader.eval(coord)` | Texture sample |

---

## 3. Function Mapping

### Math Functions

| GLSL | AGSL | Example |
|------|------|---------|
| `mix(a, b, t)` | `mix(a, b, t)` | Color blending |
| `step(edge, x)` | `step(edge, x)` | Hard threshold |
| `smoothstep(e0, e1, x)` | `smoothstep(e0, e1, x)` | Soft threshold |
| `clamp(x, min, max)` | `clamp(x, min, max)` | Constrain range |
| `fract(x)` | `fract(x)` | Fractional part |
| `mod(x, y)` | `mod(x, y)` | Modulo (limited) |
| `sin(x), cos(x), tan(x)` | `sin(x), cos(x), tan(x)` | Trigonometry |
| `pow(x, y)` | `pow(x, y)` | Power function |
| `sqrt(x)` | `sqrt(x)` | Square root |
| `abs(x)` | `abs(x)` | Absolute value |
| `min(a, b), max(a, b)` | `min(a, b), max(a, b)` | Min/Max |
| `length(v)` | `length(v)` | Vector magnitude |
| `distance(a, b)` | `length(a - b)` | Distance between |
| `dot(a, b)` | `dot(a, b)` | Dot product |
| `cross(a, b)` | `cross(a, b)` | Cross product |
| `normalize(v)` | `normalize(v)` | Unit vector |
| `reflect(I, N)` | `reflect(I, N)` | Reflection vector |
| `refract(I, N, eta)` | `refract(I, N, eta)` | Refraction |

### Special Functions

| GLSL | AGSL | Use Case |
|------|------|----------|
| `floor(x)` | `floor(x)` | Integer truncation |
| `ceil(x)` | `ceil(x)` | Round up |
| `round(x)` | `round(x)` | Round nearest |
| `sign(x)` | `sign(x)` | Sign of number |
| `exp(x)` | `exp(x)` | e^x |
| `log(x)` | `log(x)` | Natural log |
| `exp2(x)` | `exp2(x)` | 2^x |
| `log2(x)` | `log2(x)` | Log base 2 |
| `radians(deg)` | `radians(deg)` | Degrees to radians |
| `degrees(rad)` | `degrees(rad)` | Radians to degrees |
| `atan(y, x)` | `atan(y, x)` | Arctangent 2-arg |
| `asin(x)` | `asin(x)` | Arcsin |
| `acos(x)` | `acos(x)` | Arccos |

---

## 4. 20,000 Particle Simulation Techniques

### Technique 1: Fragment Shader Particles (GPU-Accelerated)

Instead of managing 20,000 particles in Kotlin (CPU), calculate them in the shader:

```glsl
// AGSL: Generate particles procedurally
for(float i = 0.0; i < 50.0; i++) {
    float2 pos = hash22(float2(i, iTime)); // Pseudo-random position
    float dist = length(uv - pos);
    float glow = smoothstep(0.01, 0.0, dist);
    color += half3(1.0, 0.0, 1.0) * glow;
}
```

**Advantages:**
- Zero CPU overhead
- Parallel GPU execution
- 60fps at 20k+ particles

**Limitations:**
- AGSL loop count limits (solvable with tiling)
- No random access to individual particles

### Technique 2: Compute Shader Style (API 33+)

Use multiple shader passes with `RenderEffect` chaining.

### Technique 3: Hybrid Approach (Recommended)

```
High-activity particles (1000)    →  Kotlin + Canvas
Background field (19000)          →  AGSL Shader
Bloodstream trails                →  AGSL Shader
Ghost shimmer overlays            →  AGSL Shader
```

---

## 5. Noise Functions (Essential for Organic Movement)

### Simplex Noise (AGSL Implementation)

```glsl
// 2D Simplex Noise - WebGL standard → AGSL
float snoise(float2 v) {
    const float4 C = float4(0.211324865405187, 0.366025403784439,
                           -0.577350269189626, 0.024390243902439);
    float2 i  = floor(v + dot(v, C.yy));
    float2 x0 = v - i + dot(i, C.xx);
    float2 i1 = (x0.x > x0.y) ? float2(1.0, 0.0) : float2(0.0, 1.0);
    float4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;
    i = mod289(i);
    float3 p = permute(permute(i.y + float3(0.0, i1.y, 1.0))
                             + i.x + float3(0.0, i1.x, 1.0));
    float3 m = max(0.5 - float3(dot(x0,x0), dot(x12.xy,x12.xy),
                               dot(x12.zw,x12.zw)), 0.0);
    m = m*m; m = m*m;
    float3 x = 2.0 * fract(p * C.www) - 1.0;
    float3 h = abs(x) - 0.5;
    float3 ox = floor(x + 0.5);
    float3 a0 = x - ox;
    m *= 1.79284291400159 - 0.85373472095314 * (a0*a0 + h*h);
    float3 g;
    g.x = a0.x * x0.x + h.x * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}
```

### Voronoi Noise (Cellular Patterns)

```glsl
float2 voronoi(float2 x) {
    float2 n = floor(x);
    float2 f = fract(x);
    float2 mg, mr;
    float md = 8.0;
    for(int j = -1; j <= 1; j++) {
        for(int i = -1; i <= 1; i++) {
            float2 g = float2(float(i), float(j));
            float2 o = float2(snoise(n + g), snoise(n + g + 1.0));
            float2 r = g + o - f;
            float d = dot(r, r);
            if(d < md) { md = d; mr = r; mg = g; }
        }
    }
    return mr;
}
```

---

## 6. Rebellious Morph Rendering

### Neon Splat Shader

```glsl
half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution.xy;
    float dist = length(uv - origin);
    float angle = atan(uv.y - origin.y, uv.x - origin.x);
    
    // Radial explosion with drip
    float ring = smoothstep(0.1, 0.0, abs(dist - progress));
    float drips = sin(angle * 12.0 + time * 10.0) * 0.5 + 0.5;
    float dripMask = smoothstep(0.3, 0.0, dist) * drips;
    
    // Neon colors
    half3 magenta = half3(1.0, 0.0, 1.0);
    half3 cyan = half3(0.0, 0.9, 1.0);
    half3 color = mix(magenta, cyan, drips);
    
    return half4(color, ring + dripMask * 0.5);
}
```

### Implementation in Kotlin

```kotlin
val splatShader = ShaderForge.createRebelliousSplatShader(
    originX = 0.5f,
    originY = 0.5f
)

// Animate the splat
LaunchedEffect(Unit) {
    while(true) {
        delay(16)
        time += 0.016f
        splatShader.setFloatUniform("iTime", time)
        splatShader.setFloatUniform("progress", time / duration)
    }
}
```

---

## 7. Ghost Shimmer for Third-Party Apps

### Emotional Color Mapping

```kotlin
enum class EmotionalValence(val shaderColor: Color) {
    MELANCHOLIC(Color(0xFF6B5B95)),  // Spotify sad music
    EUPHORIC(Color(0xFFFFD93D)),     // Energetic beats
    CURIOUS(Color(0xFF00E5FF)),      // Browser exploring
    SECRETIVE(Color(0xFF1A1A2E)),    // Incognito mode
    ANXIOUS(Color(0xFFFF6B6B)),      // Social media doomscroll
    INTENSE(Color(0xFFFF00FF)),      // Gaming session
    FOCUSED(Color(0xFF4ECDC4)),      // Work/productivity
    INSPIRED(Color(0xFFFFA07A))      // Creative tools
}
```

### Shader Implementation

```glsl
half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution.xy;
    
    // Slow wave shimmer
    float wave = sin(uv.x * 10.0 + iTime * 0.5) * 
                 cos(uv.y * 8.0 + iTime * 0.3);
    
    // Edge vignette for ghost aura
    float edge = 1.0 - 4.0 * uv.x * (1.0 - uv.x) * uv.y * (1.0 - uv.y);
    edge = pow(edge, 3.0);
    
    half3 color = half3(emotionalColor) * wave * edge * intensity;
    
    return half4(color, wave * edge * 0.3);
}
```

---

## 8. Performance Optimization

### AGSL Best Practices

| Technique | Impact | Use When |
|-----------|--------|----------|
| `half` vs `float` | 2x speed | Colors, UVs |
| Limited loops | Critical | Pre-unroll where possible |
| Uniform updates | Moderate | Batch updates per frame |
| Shader reuse | High | Same effect, different params |
| Canvas clipping | High | Don't draw off-screen |

### 20k Particle Budget

```
AGSL Shader Loop Limit: ~100 iterations
Workaround: Multiple shaders × 100 = 10,000
Hybrid: 1,000 Canvas + 19,000 AGSL = 20,000
```

---

## 9. ShaderToy to AGSL Converter

### Input (ShaderToy):

```glsl
void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec2 uv = fragCoord / iResolution.xy;
    float d = length(uv - 0.5);
    fragColor = vec4(d, 0.0, 1.0 - d, 1.0);
}
```

### Output (AGSL):

```glsl
half4 main(float2 fragCoord) {
    float2 uv = fragCoord / iResolution.xy;
    float d = length(uv - 0.5);
    return half4(d, 0.0, 1.0 - d, 1.0);
}
```

### Automated Regex:

```
void mainImage\(out vec4 (\w+), in vec2 (\w+)\)
→ half4 main(float2 $2)

$1 = 
→ return 

vec4/half4/vec3/half3/vec2/half2
→ Use half precision for colors
```

---

## 10. Integration with ParticleBloodstreamEngine

```kotlin
// Layer 1: Background shader (AGSL)
Box(modifier = Modifier.fillMaxSize()) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(shader = neuralBloodstreamShader)
    }
}

// Layer 2: Interactive particles (Canvas)
Canvas(modifier = Modifier.fillMaxSize()) {
    activeParticles.forEach { drawParticle(it) }
}

// Layer 3: Ghost shimmer overlay (AGSL)
Canvas(modifier = Modifier.fillMaxSize()) {
    drawRect(shader = ghostShimmerShader, blendMode = BlendMode.Screen)
}
```

---

## Quick Reference Card

```
╔════════════════════════════════════════════════════════════╗
║  AGSL SHADER TEMPLATE                                      ║
╠════════════════════════════════════════════════════════════╣
║  uniform shader composableContent; // For content sampling ║
║  uniform float2 iResolution;                            ║
║  uniform float iTime;                                   ║
║                                                           ║
║  half4 main(float2 fragCoord) {                         ║
║      float2 uv = fragCoord / iResolution.xy;            ║
║      half3 color = half3(0.0);                          ║
║      // Your shader code here                            ║
║      return half4(color, 1.0);                          ║
║  }                                                        ║
╚════════════════════════════════════════════════════════════╝

╔════════════════════════════════════════════════════════════╗
║  KOTLIN USAGE PATTERN                                      ║
╠════════════════════════════════════════════════════════════╣
║  val shader = RuntimeShader(agslCode)                     ║
║  shader.setFloatUniform("iTime", time)                  ║
║  shader.setFloatUniform("iResolution", width, height)   ║
║                                                           ║
║  Canvas(modifier = Modifier.fillMaxSize()) {            ║
║      drawRect(shader = shader.asComposeShader())        ║
║  }                                                        ║
╚════════════════════════════════════════════════════════════╝
```

---

**The visual nervous system now has GPU-accelerated blood.**

Ready for: Morph detection binding, third-party app shimmer triggers, or ChronoKineticForge screen integration.
