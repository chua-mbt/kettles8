package org.akaii.kettles8.shaders

import org.jetbrains.skia.Data
import java.nio.ByteBuffer
import java.nio.ByteOrder

object CRT: SkslShader {
    override val raw: String
        get() = """
        uniform shader inputImage;
        uniform vec2 resolution;
        uniform float curvature;
        uniform float scanlineStrength;
    
        half4 main(vec2 fragCoord) {
            vec2 uv = fragCoord / resolution;
            vec2 offset = uv - 0.5;
            float dist = length(offset);
        
            float curvatureFactor = 1.0 + curvature * dist * dist;
            vec2 curvedUV = 0.5 + offset * curvatureFactor;
            curvedUV = clamp(curvedUV, 0.0, 1.0);
        
            vec4 color = inputImage.eval(curvedUV * resolution);
        
            float scanline = sin(fragCoord.y * 10.0) * scanlineStrength;
            color.rgb -= scanline;
        
            color.r = inputImage.eval((curvedUV + vec2( 0.002, 0.0)) * resolution).r;
            color.b = inputImage.eval((curvedUV + vec2(-0.002, 0.0)) * resolution).b;
        
            float vignette = smoothstep(0.2, 0.9, dist);
            color.rgb *= mix(1.0, 0.3, vignette);  
        
            return color;
        }
    """.trimIndent()

    override fun uniforms(displayWidth: Int, displayHeight: Int): Data {
        val curvature = 0.2f
        val scanlineStrength = 0.05f
        val uniformSize = 16

        val crtDataBuffer = ByteBuffer.allocate(uniformSize).order(ByteOrder.LITTLE_ENDIAN)
        crtDataBuffer.putFloat(0, displayWidth.toFloat())
        crtDataBuffer.putFloat(4, displayHeight.toFloat())
        crtDataBuffer.putFloat(8, curvature)
        crtDataBuffer.putFloat(12, scanlineStrength)
        return Data.makeFromBytes(crtDataBuffer.array())
    }

}
