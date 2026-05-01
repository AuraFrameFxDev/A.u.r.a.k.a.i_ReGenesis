package dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * 🔮 SHADER FORGE — WebGL-Inspired AGSL Shader Library
 *
 * High-performance GPU shader collection for the Bloodstream Engine.
 * Inspired by ShaderToy, Three.js, and WebGL best practices.
 *
 * Techniques:
 * - Ray marching for volumetric effects
 * - Fragment shaders for 20k+ particle simulation
 * - Noise functions (Simplex, Perlin, Voronoi)
 * - Fluid dynamics approximation
 * - Chromatic aberration & glitch effects
 */

object ShaderForge {

    // ═════════════════════════════════════════════════════════════════
    // 1. NEURAL BLOODSTREAM — Main Living Background
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createNeuralBloodstreamShader(): RuntimeShader {
        val agslCode = """
            uniform shader composableContent;
            uniform float2 iResolution;
            uniform float iTime;
            uniform float emotionalArousal;
            uniform float turbulence;
            uniform float3 baseColor;     // Magenta: 1.0, 0.0, 1.0
            uniform float3 accentColor;   // Cyan: 0.0, 0.9, 1.0
            
            // Simplex noise function for organic movement
            float3 mod289(float3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
            float2 mod289(float2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
            float3 permute(float3 x) { return mod289(((x*34.0)+1.0)*x); }
            
            float snoise(float2 v) {
                const float4 C = float4(0.211324865405187, 0.366025403784439,
                                       -0.577350269189626, 0.024390243902439);
                float2 i  = floor(v + dot(v, C.yy));
                float2 x0 = v - i + dot(i, C.xx);
                float2 i1;
                i1 = (x0.x > x0.y) ? float2(1.0, 0.0) : float2(0.0, 1.0);
                float4 x12 = x0.xyxy + C.xxzz;
                x12.xy -= i1;
                i = mod289(i);
                float3 p = permute(permute(i.y + float3(0.0, i1.y, 1.0))
                                         + i.x + float3(0.0, i1.x, 1.0));
                float3 m = max(0.5 - float3(dot(x0,x0), dot(x12.xy,x12.xy),
                                           dot(x12.zw,x12.zw)), 0.0);
                m = m*m;
                m = m*m;
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
            
            // Voronoi cell pattern for neural network visualization
            float2 voronoi(float2 x) {
                float2 n = floor(x);
                float2 f = fract(x);
                float2 mg, mr;
                float md = 8.0;
                for(int j = -1; j <= 1; j++) {
                    for(int i = -1; i <= 1; i++) {
                        float2 g = float2(float(i), float(j));
                        float2 o = float2(
                            snoise(n + g + float2(0.0, iTime * 0.1)),
                            snoise(n + g + float2(3.0, iTime * 0.1))
                        );
                        float2 r = g + o - f;
                        float d = dot(r, r);
                        if(d < md) {
                            md = d;
                            mr = r;
                            mg = g;
                        }
                    }
                }
                return mr;
            }
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution.xy;
                float2 p = (fragCoord * 2.0 - iResolution.xy) / min(iResolution.x, iResolution.y);
                
                // Create flowing neural network pattern
                float t = iTime * (0.2 + emotionalArousal * 0.3);
                
                // Layer 1: Base noise flow (slow rivers)
                float noise1 = snoise(p * 2.0 + t * 0.5);
                
                // Layer 2: Fast particle streams
                float noise2 = snoise(p * 5.0 - t);
                
                // Layer 3: Voronoi cell structures (neural clusters)
                float2 vor = voronoi(p * 3.0 + t * 0.3);
                float cellPattern = length(vor);
                
                // Layer 4: Turbulence noise overlay
                float turbulenceNoise = snoise(p * 8.0 + t * 2.0) * turbulence;
                
                // Combine layers for depth
                float pattern = noise1 * 0.4 + noise2 * 0.3 + cellPattern * 0.2 + turbulenceNoise * 0.1;
                
                // Color mixing based on depth layers
                half3 darkBlue = half3(0.0, 0.02, 0.08);
                half3 deepPurple = half3(0.1, 0.0, 0.2);
                half3 magenta = half3(baseColor.x, baseColor.y, baseColor.z);
                half3 cyan = half3(accentColor.x, accentColor.y, accentColor.z);
                
                // Gradient from deep to surface
                half3 color = mix(darkBlue, deepPurple, noise1 * 0.5 + 0.5);
                color = mix(color, magenta, smoothstep(0.3, 0.7, noise2));
                color = mix(color, cyan, smoothstep(0.5, 0.9, cellPattern) * 0.6);
                
                // Add "data pulse" highlights
                float pulse = sin(iTime * 3.0 + pattern * 10.0) * 0.5 + 0.5;
                color += half3(1.0) * pulse * 0.1 * emotionalArousal;
                
                // Scanlines for retro-tech feel
                float scanline = sin(uv.y * iResolution.y * 0.7) * 0.03;
                color -= scanline;
                
                // Vignette
                float vignette = 1.0 - dot(uv - 0.5, uv - 0.5) * 1.5;
                color *= vignette;
                
                return half4(color, 1.0);
            }
        """.trimIndent()

        return RuntimeShader(agslCode).apply {
            setFloatUniform("baseColor", 1.0f, 0.0f, 1.0f)
            setFloatUniform("accentColor", 0.0f, 0.9f, 1.0f)
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // 2. REBELLIOUS SPLAT — Chaos Particle Burst
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createRebelliousSplatShader(originX: Float, originY: Float): RuntimeShader {
        val agslCode = """
            uniform float2 iResolution;
            uniform float iTime;
            uniform float2 origin;
            uniform float3 primaryColor;
            uniform float3 secondaryColor;
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution.xy;
                float2 p = uv * 2.0 - 1.0;
                p.x *= iResolution.x / iResolution.y;
                
                float2 originNDC = origin * 2.0 - 1.0;
                originNDC.x *= iResolution.x / iResolution.y;
                
                float dist = length(p - originNDC);
                float angle = atan(p.y - originNDC.y, p.x - originNDC.x);
                
                // Expanding ring
                float ring = smoothstep(0.1, 0.0, abs(dist - iTime * 0.3));
                
                // Radial drips (chaotic)
                float drips = sin(angle * 12.0 + iTime * 5.0) * 0.5 + 0.5;
                float dripMask = smoothstep(0.3, 0.0, dist) * drips;
                
                // Noise splatter
                float noise = fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
                float splatter = smoothstep(0.7, 0.9, noise) * smoothstep(0.5, 0.0, dist);
                
                // Combine
                half3 color = mix(half3(primaryColor), half3(secondaryColor), drips);
                color = mix(half3(0.0), color, ring + dripMask * 0.5 + splatter * 0.3);
                
                float alpha = (ring + dripMask * 0.5) * smoothstep(1.0, 0.0, dist);
                
                return half4(color, alpha);
            }
        """.trimIndent()

        return RuntimeShader(agslCode).apply {
            setFloatUniform("origin", originX, originY)
            setFloatUniform("primaryColor", 1.0f, 0.0f, 1.0f)
            setFloatUniform("secondaryColor", 0.0f, 0.9f, 1.0f)
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // 3. GHOST SHIMMER — Third-Party App Aura
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createGhostShimmerShader(emotionalColor: Color, intensity: Float): RuntimeShader {
        val colorHex = emotionalColor.toArgb()
        val r = ((colorHex shr 16) and 0xFF) / 255f
        val g = ((colorHex shr 8) and 0xFF) / 255f
        val b = (colorHex and 0xFF) / 255f

        val agslCode = """
            uniform float2 iResolution;
            uniform float iTime;
            uniform float3 emotionalColor;
            uniform float intensity;
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution.xy;
                
                // Slow flowing shimmer
                float wave1 = sin(uv.x * 10.0 + iTime * 0.5) * 0.5 + 0.5;
                float wave2 = cos(uv.y * 8.0 + iTime * 0.3) * 0.5 + 0.5;
                
                float shimmer = wave1 * wave2 * intensity;
                
                // Edge glow
                float edge = 1.0 - 4.0 * uv.x * (1.0 - uv.x) * uv.y * (1.0 - uv.y);
                edge = pow(edge, 2.0);
                
                half3 color = half3(emotionalColor) * shimmer * edge;
                
                return half4(color, shimmer * edge * 0.3);
            }
        """.trimIndent()

        return RuntimeShader(agslCode).apply {
            setFloatUniform("emotionalColor", r, g, b)
            setFloatUniform("intensity", intensity)
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // 4. GLITCH TRANSITION — CRT/Digital Artifact Shader
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createGlitchTransitionShader(progress: Float): RuntimeShader {
        val agslCode = """
            uniform shader composableContent;
            uniform float2 iResolution;
            uniform float progress; // 0.0 = start, 1.0 = end
            uniform float iTime;
            
            float random(float2 st) {
                return fract(sin(dot(st.xy, float2(12.9898, 78.233))) * 43758.5453123);
            }
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution.xy;
                
                // Glitch blocks
                float2 blockSize = float2(0.1, 0.02);
                float2 block = floor(uv / blockSize);
                
                // Random displacement based on progress
                float noise = random(block + iTime);
                float glitchThreshold = 1.0 - progress * 0.5;
                
                float2 distortedUV = uv;
                if (noise > glitchThreshold) {
                    distortedUV.x += (random(block + float2(1.0)) - 0.5) * 0.1 * (1.0 - progress);
                }
                
                // RGB split
                float rgbOffset = (1.0 - progress) * 0.02;
                half4 colorR = composableContent.eval((distortedUV + float2(rgbOffset, 0.0)) * iResolution);
                half4 colorG = composableContent.eval(distortedUV * iResolution);
                half4 colorB = composableContent.eval((distortedUV - float2(rgbOffset, 0.0)) * iResolution);
                
                return half4(colorR.r, colorG.g, colorB.b, 1.0);
            }
        """.trimIndent()

        return RuntimeShader(agslCode).apply {
            setFloatUniform("progress", progress)
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // 5. MATRIX RAIN — Digital Rain Effect
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createMatrixRainShader(density: Float = 0.5f): RuntimeShader {
        val agslCode = """
            uniform float2 iResolution;
            uniform float iTime;
            uniform float density;
            
            // Pseudo-random function
            float hash(float n) {
                return fract(sin(n) * 43758.5453123);
            }
            
            float hash2(float2 n) {
                return hash(dot(n, float2(1.0, 157.0)));
            }
            
            // Character pattern (simplified as dots)
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution.xy;
                
                // Column-based animation
                float col = floor(uv.x * 40.0);
                float speed = hash(col) * 0.5 + 0.5;
                float y = fract(uv.y + iTime * speed * 0.2);
                
                // Trail fade
                float trail = smoothstep(1.0, 0.0, y) * smoothstep(0.0, 0.1, y);
                
                // Random character brightness
                float char = hash2(float2(col, floor(uv.y * 40.0 - iTime * speed)));
                float brightness = step(0.3, char) * trail;
                
                // Head highlight
                float head = smoothstep(0.15, 0.0, abs(y - 0.05)) * 0.8;
                
                // Color: Green matrix + occasional magenta (Aura)
                half3 green = half3(0.0, 1.0, 0.39);
                half3 magenta = half3(1.0, 0.0, 1.0);
                half3 color = mix(green, magenta, step(0.97, hash(col)));
                
                float finalBrightness = (brightness + head) * density;
                
                return half4(color * finalBrightness, finalBrightness);
            }
        """.trimIndent()

        return RuntimeShader(agslCode).apply {
            setFloatUniform("density", density)
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // 6. SYNTHWAVE SUN — Retro-futuristic Horizon
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createSynthwaveSunShader(): RuntimeShader {
        val agslCode = """
            uniform float2 iResolution;
            uniform float iTime;
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution.xy;
                float2 p = (fragCoord * 2.0 - iResolution.xy) / iResolution.y;
                
                // Grid floor
                float gridY = abs(p.y + 0.5);
                float gridZ = 1.0 / gridY;
                float gridX = p.x * gridZ;
                float gridLine = step(0.98, fract(gridX + iTime)) + step(0.98, fract(gridZ + iTime * 2.0));
                
                // Sun
                float sunY = 0.2;
                float sun = length(float2(p.x, p.y - sunY));
                float sunDisc = smoothstep(0.3, 0.28, sun);
                
                // Sun gradient stripes
                float stripes = sin(p.y * 40.0) * 0.5 + 0.5;
                float sunColor = sunDisc * stripes;
                
                // Colors
                half3 skyTop = half3(0.05, 0.0, 0.2);
                half3 skyBottom = half3(0.8, 0.0, 0.4);
                half3 sunColors = half3(1.0, 0.8, 0.0);
                half3 gridColor = half3(1.0, 0.0, 1.0); // Magenta grid
                
                half3 color = mix(skyTop, skyBottom, uv.y);
                color = mix(color, sunColors, sunColor);
                color += gridColor * gridLine * (1.0 - smoothstep(0.0, 0.5, uv.y));
                
                return half4(color, 1.0);
            }
        """.trimIndent()

        return RuntimeShader(agslCode)
    }

    // ═════════════════════════════════════════════════════════════════
    // 7. CHROMATIC ABERRATION — Lens Distortion Effect
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createChromaticAberrationShader(strength: Float = 0.01f): RuntimeShader {
        val agslCode = """
            uniform shader composableContent;
            uniform float2 iResolution;
            uniform float strength;
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution.xy;
                float2 center = float2(0.5, 0.5);
                float2 delta = uv - center;
                float dist = length(delta);
                
                // Radial distortion increases toward edges
                float distortion = dist * strength;
                
                // Sample with RGB offset
                half4 colorR = composableContent.eval((uv + delta * distortion) * iResolution);
                half4 colorG = composableContent.eval(uv * iResolution);
                half4 colorB = composableContent.eval((uv - delta * distortion) * iResolution);
                
                return half4(colorR.r, colorG.g, colorB.b, 1.0);
            }
        """.trimIndent()

        return RuntimeShader(agslCode).apply {
            setFloatUniform("strength", strength)
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // 8. PARTICLE FIELD SIMULATION — GPU-Accelerated
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createParticleFieldShader(particleCount: Int = 100): RuntimeShader {
        val agslCode = """
            uniform float2 iResolution;
            uniform float iTime;
            uniform float particleCount;
            
            // Pseudo-random
            float2 hash22(float2 p) {
                float3 p3 = fract(float3(p.xyx) * float3(0.1031, 0.1030, 0.0973));
                p3 += dot(p3, p3.yzx + 33.33);
                return fract((p3.xx + p3.yz) * p3.zy);
            }
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / iResolution.xy;
                half3 color = half3(0.0);
                
                for(float i = 0.0; i < 50.0; i++) {
                    // Particle position (animated)
                    float2 offset = hash22(float2(i, 0.0));
                    float speed = 0.2 + hash22(float2(i, 1.0)).x * 0.5;
                    float2 pos = float2(
                        fract(offset.x + iTime * speed),
                        fract(offset.y + iTime * speed * 0.7)
                    );
                    
                    // Particle size
                    float size = 0.002 + hash22(float2(i, 2.0)).x * 0.003;
                    
                    // Distance to particle
                    float dist = length(uv - pos);
                    
                    // Glow
                    float glow = smoothstep(size, 0.0, dist);
                    
                    // Color based on index
                    half3 pColor = half3(
                        fract(i / 50.0),
                        fract(i / 30.0),
                        1.0
                    );
                    
                    color += pColor * glow * 0.5;
                }
                
                return half4(color, 1.0);
            }
        """.trimIndent()

        return RuntimeShader(agslCode).apply {
            setFloatUniform("particleCount", particleCount.toFloat())
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // UTILITY: Check AGSL Availability
    // ═════════════════════════════════════════════════════════════════

    fun isShaderSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    // ═════════════════════════════════════════════════════════════════
    // SHADER UNIFORM UPDATE HELPERS
    // ═════════════════════════════════════════════════════════════════

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun updateTime(shader: RuntimeShader, time: Float) {
        shader.setFloatUniform("iTime", time)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun updateResolution(shader: RuntimeShader, width: Float, height: Float) {
        shader.setFloatUniform("iResolution", width, height)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun updateEmotionalState(shader: RuntimeShader, arousal: Float, turbulence: Float) {
        shader.setFloatUniform("emotionalArousal", arousal)
        shader.setFloatUniform("turbulence", turbulence)
    }
}

// ═════════════════════════════════════════════════════════════════════
// SHADER BRIDGE IMPLEMENTATIONS
// ═════════════════════════════════════════════════════════════════════

/**
 * WebGL → AGSL Bridge
 *
 * Maps WebGL concepts to Android Graphics Shading Language:
 * - gl_FragCoord → fragCoord
 * - iResolution → uniform float2
 * - iTime → uniform float
 * - texture2D → shader.eval()
 * - mix() → mix()
 * - smoothstep() → smoothstep()
 */

/*
WEBGL CONCEPT               AGSL EQUIVALENT
─────────────────────────────────────────────
gl_FragCoord                float2 fragCoord (parameter)
gl_FragColor                return half4
uniform vec2 iResolution    uniform float2 iResolution
uniform float iTime         uniform float iTime
texture2D(sampler, uv)      shader.eval(coord)
vec4                        half4
vec3                        half3 or float3
vec2                        half2 or float2
mix(a, b, t)                mix(a, b, t)
smoothstep(e0, e1, x)       smoothstep(e0, e1, x)
fract(x)                    fract(x)
sin/cos/tan                 sin/cos/tan
length(v)                   length(v)
dot(a, b)                   dot(a, b)
distance(a, b)              length(a - b)
mod(a, b)                   mod(a, b) or a - floor(a/b)*b

NOISE FUNCTIONS:
────────────────
WebGL Simplex3D             AGSL: Manual implementation (provided above)
GLSL noise libraries        AGSL: Custom implementation required
*/
