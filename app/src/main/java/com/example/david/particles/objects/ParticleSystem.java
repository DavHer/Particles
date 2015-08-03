package com.example.david.particles.objects;

import android.graphics.Color;

import com.example.david.particles.data.VertexArray;
import com.example.david.particles.programs.ParticleShaderProgram;
import com.example.david.particles.util.Geometry;

import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.GL_POINTS;

/**
 * Created by david on 21/07/15.
 */
public class ParticleSystem {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;

    private static final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT +
                                                    COLOR_COMPONENT_COUNT+
                                                    VECTOR_COMPONENT_COUNT+
                                                    PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    private final float[] particles;
    private final VertexArray vertexArray;
    private int maxParticleCount;

    private int currentParticleCount;
    private int nextParticle;

    public ParticleSystem(int maxParticleCount){
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
    }

    public void addParticle(Geometry.Point position, int color, Geometry.Vector direction,
                            float particleStartTime){

        int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;

        int currentOffset = particleOffset;
        nextParticle++;

        if(nextParticle>maxParticleCount)
            return;

        if(currentParticleCount < maxParticleCount){
            currentParticleCount++;
        }
        if(nextParticle == maxParticleCount){
            //Start over at the beginning, but keep currentParticleCount so
            //that all the other particles still get draw
            nextParticle = 0;
        }

        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        particles[currentOffset++] = Color.red(color)/255f;
        particles[currentOffset++] = Color.green(color)/255f;
        particles[currentOffset++] = Color.blue(color)/255f;

        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;

        particles[currentOffset++] = particleStartTime;

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(ParticleShaderProgram particleProgram){
        int dataOffset = 0;
        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getColorLocation(),
                COLOR_COMPONENT_COUNT, STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getDirectionVectorLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset, particleProgram.getParticleStartTimeLocation(),
                PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE);
    }

    public void draw(){
        glDrawArrays(GL_POINTS, 0, currentParticleCount);
    }
}
