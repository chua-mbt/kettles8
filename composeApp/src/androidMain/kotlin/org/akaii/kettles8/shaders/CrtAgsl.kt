package org.akaii.kettles8.shaders

import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
object CrtAgsl : AgslShader {
    override val raw: String
        get() = """
            uniform shader inputImage;
            uniform float2 resolution;
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / resolution;
                float2 offset = uv - 0.5;
                float dist = length(offset);
            
                float curvature = 0.2;
                float curvatureFactor = 1.0 + curvature * dist * dist;
                float2 curvedUV = 0.5 + offset * curvatureFactor;
                curvedUV = clamp(curvedUV, 0.0, 1.0);
            
                half4 color = inputImage.eval(curvedUV * resolution);
                
                color.r = inputImage.eval((curvedUV + float2(0.004, 0.0)) * resolution).r;
                color.b = inputImage.eval((curvedUV + float2(-0.004, 0.0)) * resolution).b;
            
                float brightnessFactor = 0.7;
                color.rgb *= brightnessFactor;
            
                float bloomThreshold = 0.2; 
                float bloomIntensity = 0.3; 
                float bloomSpread = 0.004;
            
                float randomOffsetX = sin(fragCoord.x * 0.1) * 0.003;
                float randomOffsetY = cos(fragCoord.y * 0.1) * 0.003;
                float2 randomOffset = float2(randomOffsetX, randomOffsetY);
            
                float3 bloom = inputImage.eval((curvedUV + float2(bloomSpread, bloomSpread) + randomOffset) * resolution).rgb +
                               inputImage.eval((curvedUV + float2(-bloomSpread, -bloomSpread) - randomOffset) * resolution).rgb +
                               inputImage.eval((curvedUV + float2(bloomSpread, -bloomSpread) + randomOffset) * resolution).rgb +
                               inputImage.eval((curvedUV + float2(-bloomSpread, bloomSpread) - randomOffset) * resolution).rgb;
            
                bloom *= 0.25;
                bloom = smoothstep(bloomThreshold, 1.0, bloom) * bloomIntensity;
            
                color.rgb += bloom;
            
                float vignette = smoothstep(0.2, 0.9, dist);
                color.rgb *= mix(1.0, 0.3, vignette);
            
                float scanlineStrength = 0.08;
                float scanline = sin(fragCoord.y * 10.0) * scanlineStrength;
                color.rgb -= scanline * (1.0 - bloom * 0.5);
            
                return color;
            }
        """.trimIndent()

    override fun arguments(displayWidth: Int, displayHeight: Int): FloatArray {
        return floatArrayOf(displayWidth.toFloat(), displayHeight.toFloat())
    }
}
