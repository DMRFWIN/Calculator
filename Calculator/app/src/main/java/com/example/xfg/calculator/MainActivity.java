package com.example.xfg.calculator;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    //0~9十个按键
    private Button[] btn = new Button[10];
    //显示器，用于指示显示结果
    private EditText input;
    //显示器下方的记忆器，用于记录上一次运算结果
    private TextView mem;
    //三角计算时的标志显示，角度还是弧度
    private TextView _drg;
    //小提示，用于加强人机交互的弱检测、提示功能
    private TextView tip;

    private Button
            div, mul, sub, add, equal, // "/","*","-","+","="
            sin, cos, tan, log, ln,
            sqrt, square, factorial, bksp,//根号、平方，阶乘，后退键
            left, right, dot, exit, drg,//左括号，右括号，小数点，退出，角度弧度控制键
            mc, c;//mem清屏键，input清屏键
    //保存原来算式的样子，为了输出时好看，因为计算时算式样子会被改变
    public String str_old;
    //变换样子后的式子
    public String str_new;
    //输入控制，true为重新输入，false为接着输入
    public boolean v_begin = true;
    //控制DRG按键，true为角度，false为弧度
    public boolean drg_flag = true;
    //π值
    public double pi = 4 * Math.atan(1);
    //true表示正确，可以继续输入，false表示有误，输入被锁定
    public boolean tip_lock = true;
    //判断是不是按下=号之前的输入，true表示输入在=之前，false表示输入在=之后
    public boolean equals_flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        div = (Button) findViewById(R.id.divide);
        mul = (Button) findViewById(R.id.mul);
        sub = (Button) findViewById(R.id.sub);
        add = (Button) findViewById(R.id.add);
        equal = (Button) findViewById(R.id.equal);
        sin = (Button) findViewById(R.id.sin);
        cos = (Button) findViewById(R.id.cos);
        tan = (Button) findViewById(R.id.tan);
        log = (Button) findViewById(R.id.log);
        ln = (Button) findViewById(R.id.ln);
        sqrt = (Button) findViewById(R.id.sqrt);
        square = (Button) findViewById(R.id.square);
        factorial = (Button) findViewById(R.id.factorial);
        bksp = (Button) findViewById(R.id.bksp);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        dot = (Button) findViewById(R.id.dot);
        exit = (Button) findViewById(R.id.exit);
        drg = (Button) findViewById(R.id.drg);
        mc = (Button) findViewById(R.id.mc);
        c = (Button) findViewById(R.id.c);
        btn[0] = (Button) findViewById(R.id.zero);
        btn[1] = (Button) findViewById(R.id.one);
        btn[2] = (Button) findViewById(R.id.two);
        btn[3] = (Button) findViewById(R.id.three);
        btn[4] = (Button) findViewById(R.id.four);
        btn[5] = (Button) findViewById(R.id.five);
        btn[6] = (Button) findViewById(R.id.six);
        btn[7] = (Button) findViewById(R.id.seven);
        btn[8] = (Button) findViewById(R.id.eight);
        btn[9] = (Button) findViewById(R.id.nine);
        input = (EditText) findViewById(R.id.input);
        mem = (TextView) findViewById(R.id.mem);
        tip = (TextView) findViewById(R.id.tip);

        for (int i = 0; i < 10; i++) {
            btn[i].setOnClickListener(actionPerformed);
        }
        div.setOnClickListener(actionPerformed);
        mul.setOnClickListener(actionPerformed);
        sub.setOnClickListener(actionPerformed);
        add.setOnClickListener(actionPerformed);
        equal.setOnClickListener(actionPerformed); // "/","*","-","+","="
        sin.setOnClickListener(actionPerformed);
        cos.setOnClickListener(actionPerformed);
        tan.setOnClickListener(actionPerformed);
        log.setOnClickListener(actionPerformed);
        ln.setOnClickListener(actionPerformed);
        sqrt.setOnClickListener(actionPerformed);
        square.setOnClickListener(actionPerformed);
        factorial.setOnClickListener(actionPerformed);
        bksp.setOnClickListener(actionPerformed);
        left.setOnClickListener(actionPerformed);
        right.setOnClickListener(actionPerformed);
        dot.setOnClickListener(actionPerformed);
        exit.setOnClickListener(actionPerformed);
        drg.setOnClickListener(actionPerformed);
        mc.setOnClickListener(actionPerformed);
        c.setOnClickListener(actionPerformed);

    }

    /**
     * 键盘命令捕捉
     */
    //命令缓存，用于检测输入合法性
    String[] Tipcommand = new String[500];
    //Tipcommand的指针
    int tip_i = 0;

    private View.OnClickListener actionPerformed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //获取按键上的命令
            String command = ((Button) v).getText().toString();
            //获取显示器上的字符串
            String str = input.getText().toString();
            //检测输入是否合法
            if (equals_flag == false && "0123456789.()sincostanlnlogn!+-×÷√^".indexOf(command) != -1) {
                //检测显示器上的字符串是否合法
                if (right(str)) {
                    //如果当前按键含有"+-×÷√^)"
                    if ("+-×÷√^)".indexOf(command) != -1) {
                        for (int i = 0; i < str.length(); i++) {
                            Tipcommand[tip_i] = String.valueOf(str.charAt(i));
                            tip_i++;
                        }
                    }
                } else {
                    input.setText("0");
                    v_begin = true;
                    tip_i = 0;
                    tip_lock = true;
                    tip.setText("欢迎使用");
                }
                equals_flag = true;
            }

            if (tip_i > 0) {
                TipChecker(Tipcommand[tip_i - 1], command);
            } else if (tip_i == 0) {
                TipChecker("#", command);
            }
            //如果输入正确，就把对应按键的文本缓存到Tipcommand中
            if ("0123456789.()sincostanlnlogn!+-×÷√^".indexOf(command) != -1 && tip_lock) {
                Tipcommand[tip_i] = command;
                tip_i++;
            }
            //如果输入z正确，就将对应按键文本显示到显示器上
            if ("0123456789.()sincostanlnlogn!+-×÷√^".indexOf(command) != -1 && tip_lock) {
                print(command);
                //如果单击了DRG，就切换当前弧度角度制，并将切换结果显示到按键上方
            } else if (command.compareTo("DRG") == 0 && tip_lock) {
                if (drg_flag) {
                    drg_flag = false;
                    _drg.setText("  RAD");
                } else {
                    drg_flag = true;
                    _drg.setText("  DEG");
                }
                //如果输入的是退格键，并且是在按=之前
            } else if (command.compareTo("Bksp") == 0 && tip_lock && equals_flag) {
                //一次删除3个字符
                if (TTO(str) == 3) {
                    if (str.length() > 3) {
                        input.setText(str.substring(0, str.length() - 3));
                    } else {
                        input.setText("0");
                        v_begin = true;
                        tip_i = 0;
                        tip.setText("欢迎使用");
                    }
                    //一次删除2个字符
                } else if (TTO(str) == 2) {
                    if (str.length() > 2) {
                        input.setText(str.substring(0, str.length() - 2));
                    } else {
                        input.setText("0");
                        v_begin = true;
                        tip_i = 0;
                        tip.setText("欢迎使用");
                    }
                    //一次删除一个字符
                } else if (TTO(str) == 1) {
                    //如果之前的字符串合法则删除一个字符
                    if (right(str)) {
                        if (str.length() > 1) {
                            input.setText(str.substring(0, str.length() - 1));
                        } else {
                            input.setText("0");
                            v_begin = true;
                            tip_i = 0;
                            tip.setText("欢迎使用");
                        }
                        //如果之前的字符串不合法则全部删除
                    } else {
                        input.setText("0");
                        v_begin = true;
                        tip_i = 0;
                        tip.setText("欢迎使用");
                    }
                }
                //如果删除了字符之后只剩下一个-（比如原来是-2，删除了一位之后剩下了一个-号，他没什么存在的意义，所以应该删去）

                if (input.getText().equals("-") || equals_flag == false) {
                    input.setText("0");
                    v_begin = true;
                    tip_i = 0;
                    tip.setText("欢迎使用！");
                }
                tip_lock = true;
                if (tip_i > 0) tip_i--;
                //如果是在按下=之后按下的删除键
            } else if (command.compareTo("Bksp") == 0 && !equals_flag) {
                //将显示器清空
                input.setText("0");
                v_begin = true;////重新输入标志置为true
                tip_i = 0;
                tip_lock = true;     //表明可以继续输入
                tip.setText("欢迎使用！");
                //如果点击的是清除键
            } else if (command.compareTo("C") == 0) {
                input.setText("0");
                v_begin = true;
                tip_i = 0;
                tip_lock = true;
                tip.setText("欢迎使用！");
                equals_flag = true;//表明接下来的输入在=之前
                //如果输入的是MC，则将存储器的内容清空
            } else if (command.compareTo("MC") == 0) {
                mem.setText("0");
                //如果按exit则退出程序
            }else if(command.compareTo("EXIT")==0){
                System.exit(0);
            }


        }

    };

    private int TTO(String str) {
        return 0;
    }

    private void print(String command) {
    }

    private void TipChecker(String s, String command) {
    }

    private boolean right(String str) {
        return true;
    }
}
