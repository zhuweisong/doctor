/*
 * This file provided by Facebook is for non-commercial testing and evaluation purposes only.
 * Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.houfubao.doctor.view;

import com.houfubao.doctor.R;
import com.houfubao.doctor.R.id;
import com.houfubao.doctor.R.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class RowView extends FrameLayout{
  private  TextView mTextView;
  private  TextView mSubTextView;

  public RowView(Context context) {
    this(context, null);
  }

  public RowView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RowView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.main_app_item, this, false);
    mTextView = (TextView) view.findViewById(R.id.app_text);
    mSubTextView = (TextView) view.findViewById(R.id.question_count);
    addView(view);
  }


  public void setText(int resid, int drawrableId) {
	  mTextView.setText(resid);
	  Drawable drawable = getResources().getDrawable(drawrableId);
	  drawable.setBounds(0, 0, 100, 30);
	  mTextView.setCompoundDrawables(null, null, null, drawable);
  }
  
  public void setSubtext(String text) {
    mSubTextView.setText(text);
  }
}
