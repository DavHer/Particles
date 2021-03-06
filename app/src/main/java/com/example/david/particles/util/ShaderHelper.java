package com.example.david.particles.util;

import android.util.Log;

import static android.opengl.GLES20.*;
/**
 * Created by david on 15/02/15.
 */
public class ShaderHelper {
    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode){
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode){
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode){
        final int shaderObjectId = glCreateShader(type);

        if(shaderObjectId == 0){
            if(LoggerConfig.ON){
                Log.w(TAG, "Could not create new shader");
            }

            return 0;
        }

        glShaderSource(shaderObjectId,shaderCode);
        glCompileShader(shaderObjectId);

        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS,compileStatus,0);

        if(LoggerConfig.ON){
            // Print the shader info log to the android log output.
            Log.v(TAG, "Results of compiling source:"+ glGetShaderInfoLog(shaderObjectId));
        }
        if(compileStatus[0]==0){
            // if it failed, delete the shader object.
            glDeleteShader(shaderObjectId);
            if(LoggerConfig.ON){
                // Print the shader info log to the android log output.
                Log.w(TAG, "Compilation of shader failed.");
            }
            return 0;
        }

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId){
        final int programObjectId = glCreateProgram();

        if(programObjectId==0){
            if(LoggerConfig.ON){
                Log.w(TAG,"Could not create new program");
            }
            return 0;
        }

        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId,fragmentShaderId);
        glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS,linkStatus,0);

        if(LoggerConfig.ON){
            // Print the shader info log to the android log output.
            Log.v(TAG, "Results of linking program:"+ glGetProgramInfoLog(programObjectId));
        }
        if(linkStatus[0]==0){
            // if it failed, delete the shader object.
            glDeleteProgram(programObjectId);
            if(LoggerConfig.ON){
                // Print the shader info log to the android log output.
                Log.w(TAG, "linking program failed.");
            }
            return 0;
        }

        return programObjectId;
    }

    public static boolean validateProgram(int programObjectId){
        glValidateProgram(programObjectId);

        final int [] validateStatus = new int[1];
        glGetProgramiv(programObjectId,GL_VALIDATE_STATUS,validateStatus,0);
        Log.v(TAG,"Results of validating program: "+validateStatus[0]+"\nLog: " +glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource){
        int program;

        //Compile shaders
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        //Link them into a shader program
        program = linkProgram(vertexShader,fragmentShader);
        if(LoggerConfig.ON){
            ShaderHelper.validateProgram(program);
        }

        return program;
    }
}
