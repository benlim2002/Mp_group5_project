/*
 * Copyright 2016 winston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mp_group5_project.ui.home.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.example.mp_group5_project.ui.home.MyGLUtils;
import com.example.mp_group5_project.R;

/**
 * @author winston (1054669137@qq.com)
 */
public class ReliefFilter extends CameraFilter {
    private int program;

    public ReliefFilter(Context context) {
        super(context);
        program = MyGLUtils.buildProgram(context, R.raw.vertext, R.raw.relief);
    }

    @Override
    public void onDraw(int cameraTexId, int canvasWidth, int canvasHeight) {
        setupShaderInputs(program,
                new int[]{canvasWidth, canvasHeight},
                new int[]{cameraTexId},
                new int[][]{});
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
