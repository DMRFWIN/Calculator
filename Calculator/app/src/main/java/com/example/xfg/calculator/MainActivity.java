package com.example.xfg.calculator;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.StringTokenizer;

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
            //如果输入正确，就将对应按键文本显示到显示器上
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
            } else if (command.compareTo("EXIT") == 0) {
                System.exit(0);
                //如果是=，并且输入合法
            } else if (command.compareTo("=") == 0 && right(str) && equals_flag && tip_lock) {
                tip_i = 0;
                //设置不可继续输入
                tip_lock = false;
                //表示输入=之后
                equals_flag = false;
                //保存原来式子的样子
                str_old = str;
                //替换算式中的运算符便于计算
                str = str.replaceAll("sin", "s");
                str = str.replaceAll("cos", "c");
                str = str.replaceAll("tan", "t");
                str = str.replaceAll("log", "g");
                str = str.replaceAll("ln", "l");
                str = str.replaceAll("n!", "!");

                //重新输入表示设置为true
                v_begin = true;
                //将-1转化成-
                str = str.replaceAll("-1×", "-");
                //计算算式结果
                new calc().process(str);
            }
            tip_lock = true;
        }
    };

    /*
         * 检测函数，返回值为3、2、1  表示应当一次删除几个？  Three+Two+One = TTO
         * 为Bksp按钮的删除方式提供依据
         * 返回3，表示str尾部为sin、cos、tan、log中的一个，应当一次删除3个
         * 返回2，表示str尾部为ln、n!中的一个，应当一次删除2个
         * 返回1，表示为除返回3、2外的所有情况，只需删除一个（包含非法字符时要另外考虑：应清屏）
         */
    private int TTO(String str) {
        int l = str.length() - 1;
        if (l > 1) {
            if ((str.charAt(l) == 'n' && str.charAt(l - 1) == 'i' && str.charAt(l - 2) == 's') ||
                    (str.charAt(l) == 'n' && str.charAt(l - 1) == 'a' && str.charAt(l - 2) == 't') ||
                    (str.charAt(l) == 's' && str.charAt(l - 1) == 'o' && str.charAt(l - 2) == 'c')) {
                return 3;
            }
        }
        if (l > 0) {
            if ((str.charAt(l) == 'n' && str.charAt(l - 1) == 'l') || (str.charAt(l) == '!' && str.charAt(l - 1) == 'n')) {
                return 2;
            }
        }
        return 1;
    }

    //向input输出字符串
    private void print(String command) {
        //如果标识符是重新输入，就将原来的清空，重新设置显示文本
        if (v_begin) {
            input.setText(command);
        } else {
            //如果标识符是继续输入，则在原来的字符串追加显示现在的字符
            input.append(command);
        }
        v_begin = false;
    }

    /*
    * 检测函数，对str进行前后语法检测
    * 为Tip的提示方式提供依据，与TipShow()配合使用
    *  编号 字符    其后可以跟随的合法字符
    *   1  （                 数字|（|-|.|函数
    *   2   ）                算符|）|√ ^
    *   3  .      数字|算符|）|√ ^
    *   4   数字        .|数字|算符|）|√ ^
    *   5   算符             数字|（|.|函数
    *   6 √ ^     （ |. | 数字
    *   7  函数           数字|（|.
    *
    * 小数点前后均可省略，表示0
    * 数字第一位可以为0
    */
    private void TipChecker(String tipcommand1, String tipcommand2) {
        //Tipcode1表示错误类型，Typecode2表示名词解释类型
        int Tipecode1 = 0, Tipecode2 = 0;
        //表示命令类型
        int typepe1 = 0, typepe2 = 0;
        //括号数
        int backet = 0;
        //"+-×÷√^"不能作为第一位
        if (tipcommand1.compareTo("#") == 0 && (tipcommand2.compareTo("+") == 0 || tipcommand2.compareTo("-") == 0 ||
                tipcommand2.compareTo("×") == 0 || tipcommand2.compareTo("÷") == 0 || tipcommand2.compareTo("√") == 0 ||
                tipcommand2.compareTo("^") == 0
        )) {
            Tipecode1 = -1;
            //定义存储字符串中最后一位的类型
        } else if (tipcommand1.compareTo("#") != 0) {
            if (tipcommand1.compareTo("(") == 0) {
                typepe1 = 1;
            } else if (tipcommand1.compareTo(")") == 0) {
                typepe1 = 2;
            } else if (tipcommand1.compareTo(".") == 0) {
                typepe1 = 3;
            } else if ("0123456789".indexOf(tipcommand1) != -1) {
                typepe1 = 4;
            } else if ("+-×÷".indexOf(tipcommand1) != -1) {
                typepe1 = 5;
            } else if ("√^".indexOf(tipcommand1) != -1) {
                typepe1 = 6;
            } else if ("sincostanloglnn!".indexOf(tipcommand1) != -1) {
                typepe1 = 7;
            }
            //定义欲输入的按键类型
            if (tipcommand2.compareTo("(") == 0) {
                typepe2 = 1;
            } else if (tipcommand2.compareTo(")") == 0) {
                typepe2 = 2;
            } else if (tipcommand2.compareTo(".") == 0) {
                typepe2 = 3;
            } else if ("0123456789".indexOf(tipcommand2) != -1) {
                typepe2 = 4;
            } else if ("+-×÷".indexOf(tipcommand2) != -1) {
                typepe2 = 5;
            } else if ("√^".indexOf(tipcommand2) != -1) {
                typepe2 = 6;
            } else if ("sincostanloglnn!".indexOf(tipcommand2) != -1) {
                typepe2 = 7;
            }
        }

        switch (typepe1) {
            case 1: {
                if (typepe2 == 2 || (typepe2 == 5 && tipcommand2.compareTo("-") != 0) || typepe2 == 6) {
                    Tipecode1 = 1;
                }
                break;
            }
            case 2: {
                if (typepe2 == 1 || typepe2 == 3 || typepe2 == 4 || typepe2 == 7) {
                    Tipecode1 = 2;
                }
                break;
            }
            case 3: {
                if (typepe2 == 1 || typepe2 == 7) {
                    Tipecode1 = 3;
                }
                if (typepe2 == 3) {
                    //连续输入两个“.”
                    Tipecode1 = 8;
                }
                break;
            }
            case 4: {
                if (typepe2 == 1 || typepe2 == 7) {
                    Tipecode1 = 4;
                }
                break;
            }
            case 5: {
                if (typepe2 == 2 || typepe2 == 5 || typepe2 == 6) {
                    Tipecode1 = 5;
                }
                break;
            }
            case 6: {
                //“√^”后面直接接右括号，“+-x÷√^”以及“sincos...”
                if (typepe2 == 2 || typepe2 == 5 || typepe2 == 6 || typepe2 == 7)
                    Tipecode1 = 6;
                break;
            }
            case 7: {
                if (typepe2 == 2 || typepe2 == 5 || typepe2 == 6 || typepe2 == 7) {
                    Tipecode1 = 7;
                }
                break;
            }
        }
        //檢查小数点的重复性   Tipecode1==0表示前面检查的问题不存在
        if (Tipecode1 == 0 && tipcommand2.compareTo(".") == 0) {
            //用来计数小数点的个数
            int tip_point = 0;
            for (int i = 0; i < tip_i; i++) {
                //若之前出现一个小数点点，则小数点计数加1
                if (Tipcommand[i].compareTo(".") == 0) {
                    tip_point++;
                }
                //若出现以下几个运算符之一，小数点计数清零
                if (Tipcommand[i].compareTo("sin") == 0 || Tipcommand[i].compareTo("cos") == 0 ||
                        Tipcommand[i].compareTo("tan") == 0 || Tipcommand[i].compareTo("log") == 0 ||
                        Tipcommand[i].compareTo("ln") == 0 || Tipcommand[i].compareTo("n!") == 0 ||
                        Tipcommand[i].compareTo("√") == 0 || Tipcommand[i].compareTo("^") == 0 ||
                        Tipcommand[i].compareTo("÷") == 0 || Tipcommand[i].compareTo("×") == 0 ||
                        Tipcommand[i].compareTo("-") == 0 || Tipcommand[i].compareTo("+") == 0 ||
                        Tipcommand[i].compareTo("(") == 0 || Tipcommand[i].compareTo(")") == 0) {
                    tip_point = 0;
                }
            }
            tip_point++;
            //若小数点计数大于1，表明小数点重复了
            if (tip_point > 1) {
                Tipecode1 = 8;
            }
        }

        //检查右括号的匹配
        if (Tipecode1 == 0 && tipcommand2.compareTo(")") == 0) {
            int tip_rignt_backet = 0;
            for (int i = 0; i < tip_i; i++) {
                //每出现一个左括号，则计数加1
                if (Tipcommand[i].compareTo("(") == 0) {
                    tip_rignt_backet++;
                }
                //每出现一个右括号，则计数减1
                if (Tipcommand[i].compareTo(")") == 0) {
                    tip_rignt_backet--;
                }
            }
            //若tip_rignt_backet==0,则说明没有对应的左括号与当前的右括号匹配
            if (tip_rignt_backet == 0) {
                Tipecode1 = 10;
            }
        }


        //检查输入=的合法性
        if (Tipecode1 == 0 && tipcommand2.compareTo("=") == 0) {
            int tip_backet = 0;
            for (int i = 0; i < tip_i; i++) {
                if (Tipcommand[i].compareTo("(") == 0) {
                    tip_backet++;
                }
                if (Tipcommand[i].compareTo(")") == 0) {
                    tip_backet--;
                }
            }
            //如果tip_backet>0，就说明还有左括号未匹配上右括号
            if (tip_backet > 0) {
                Tipecode1 = 9;
                backet = tip_backet;
            } else if (tip_backet == 0) {
                //如果=前面是下列字符，=也不合法
                if ("√^sincostanloglnn!".indexOf(tipcommand1) != -1) {
                    Tipecode1 = 6;
                }
                //若前一个字符是以下之一，表明=号不合法
                if ("+-×÷".indexOf(tipcommand1) != -1) {
                    Tipecode1 = 5;
                }
            }
        }
        //若命令式以下之一，则显示相应的帮助信息
        if (tipcommand2.compareTo("MC") == 0) Tipecode2 = 1;
        if (tipcommand2.compareTo("C") == 0) Tipecode2 = 2;
        if (tipcommand2.compareTo("DRG") == 0) Tipecode2 = 3;
        if (tipcommand2.compareTo("Bksp") == 0) Tipecode2 = 4;
        if (tipcommand2.compareTo("sin") == 0) Tipecode2 = 5;
        if (tipcommand2.compareTo("cos") == 0) Tipecode2 = 6;
        if (tipcommand2.compareTo("tan") == 0) Tipecode2 = 7;
        if (tipcommand2.compareTo("log") == 0) Tipecode2 = 8;
        if (tipcommand2.compareTo("ln") == 0) Tipecode2 = 9;
        if (tipcommand2.compareTo("n!") == 0) Tipecode2 = 10;
        if (tipcommand2.compareTo("√") == 0) Tipecode2 = 11;
        if (tipcommand2.compareTo("^") == 0) Tipecode2 = 12;
        //显示帮助和错误信息
        TipShow(backet, Tipecode1, Tipecode2, tipcommand1, tipcommand2);
    }

    /*
      * 反馈Tip信息，加强人机交互，与TipChecker()配合使用
      */
    private void TipShow(int bracket, int tipcode1, int tipcode2,
                         String tipcommand1, String tipcommand2) {
        String tipmessage = "";
        if (tipcode1 != 0)
            tip_lock = false;//表明输入有误
        switch (tipcode1) {
            case -1:
                tipmessage = tipcommand2 + "  不能作为第一个算符\n";
                break;
            case 1:
                tipmessage = tipcommand1 + "  后应输入：数字/(/./-/函数 \n";
                break;
            case 2:
                tipmessage = tipcommand1 + "  后应输入：)/算符 \n";
                break;
            case 3:
                tipmessage = tipcommand1 + "  后应输入：)/数字/算符 \n";
                break;
            case 4:
                tipmessage = tipcommand1 + "  后应输入：)/./数字 /算符 \n";
                break;
            case 5:
                tipmessage = tipcommand1 + "  后应输入：(/./数字/函数 \n";
                break;
            case 6:
                tipmessage = tipcommand1 + "  后应输入：(/./数字 \n";
                break;
            case 7:
                tipmessage = tipcommand1 + "  后应输入：(/./数字 \n";
                break;
            case 8:
                tipmessage = "小数点重复\n";
                break;
            case 9:
                tipmessage = "不能计算，缺少 " + bracket + " 个 )";
                break;
            case 10:
                tipmessage = "不需要  )";
                break;
        }
        switch (tipcode2) {
            case 1:
                tipmessage = tipmessage + "[MC 用法: 清除记忆 MEM]";
                break;
            case 2:
                tipmessage = tipmessage + "[C 用法: 归零]";
                break;
            case 3:
                tipmessage = tipmessage + "[DRG 用法: 选择 DEG 或 RAD]";
                break;
            case 4:
                tipmessage = tipmessage + "[Bksp 用法: 退格]";
                break;
            case 5:
                tipmessage = tipmessage + "sin 函数用法示例：\n" +
                        "DEG：sin30 = 0.5      RAD：sin1 = 0.84\n" +
                        "注：与其他函数一起使用时要加括号，如：\n" +
                        "sin(cos45)，而不是sincos45";
                break;
            case 6:
                tipmessage = tipmessage + "cos 函数用法示例：\n" +
                        "DEG：cos60 = 0.5      RAD：cos1 = 0.54\n" +
                        "注：与其他函数一起使用时要加括号，如：\n" +
                        "cos(sin45)，而不是cossin45";
                break;
            case 7:
                tipmessage = tipmessage + "tan 函数用法示例：\n" +
                        "DEG：tan45 = 1      RAD：tan1 = 1.55\n" +
                        "注：与其他函数一起使用时要加括号，如：\n" +
                        "tan(cos45)，而不是tancos45";
                break;
            case 8:
                tipmessage = tipmessage + "log 函数用法示例：\n" +
                        "log10 = log(5+5) = 1\n" +
                        "注：与其他函数一起使用时要加括号，如：\n" +
                        "log(tan45)，而不是logtan45";
                break;
            case 9:
                tipmessage = tipmessage + "ln 函数用法示例：\n" +
                        "ln10 = le(5+5) = 2.3   lne = 1\n" +
                        "注：与其他函数一起使用时要加括号，如：\n" +
                        "ln(tan45)，而不是lntan45";
                break;
            case 10:
                tipmessage = tipmessage + "n! 函数用法示例：\n" +
                        "n!3 = n!(1+2) = 3×2×1 = 6\n" +
                        "注：与其他函数一起使用时要加括号，如：\n" +
                        "n!(log1000)，而不是n!log1000";
                break;
            case 11:
                tipmessage = tipmessage + "√ 用法示例：开任意次根号\n" +
                        "如：27开3次根为  27√3 = 3\n" +
                        "注：与其他函数一起使用时要加括号，如：\n" +
                        "(函数)√(函数) ， (n!3)√(log100) = 2.45";
                break;
            case 12:
                tipmessage = tipmessage + "^ 用法示例：开任意次平方\n" +
                        "如：2的3次方为  2^3 = 8\n" +
                        "注：与其他函数一起使用时要加括号，如：\n" +
                        "(函数)√(函数) ， (n!3)^(log100) = 36";
                break;
        }
        //将提示信息显示到tip
        tip.setText(tipmessage);
    }

    /*
     * 判断一个str是否是合法的，返回值为true、false
     * 只包含0123456789.()sincostanlnlogn!+-×÷√^的是合法的str，返回true
     * 包含了除0123456789.()sincostanlnlogn!+-×÷√^以外的字符的str为非法的，返回false
     */
    private boolean right(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '1' && str.charAt(i) != '2' && str.charAt(i) != '3' && str.charAt(i) != '4'
                    && str.charAt(5) != '1' && str.charAt(6) != '1' && str.charAt(7) != '1' && str.charAt(8) != '1' &&
                    str.charAt(i) != '9' && str.charAt(i) != '.' && str.charAt(i) != '(' && str.charAt(i) != ')' &&
                    str.charAt(i) != 's' && str.charAt(i) != 'i' && str.charAt(i) != 'n' && str.charAt(i) != 'c' && str.charAt(i) != 'o' &&
                    str.charAt(i) != 't' && str.charAt(i) != 'a' && str.charAt(i) != 'l' && str.charAt(i) != 'o' &&
                    str.charAt(i) != 'g' && str.charAt(i) != '!' && str.charAt(i) != '+' &&
                    str.charAt(i) != '×' && str.charAt(i) != '÷' && str.charAt(i) != '√' && str.charAt(i) != '^') {
                return false;
            }
        }
        return true;
    }


    /*
       * 整个计算核心，只要将表达式的整个字符串传入calc().process()就可以实行计算了
       * 算法包括以下几部分：
       * 1、计算部分           process(String str)  当然，这是建立在查错无错误的情况下
       * 2、数据格式化      FP(double n)         使数据有相当的精确度
       * 3、阶乘算法           N(double n)          计算n!，将结果返回
       * 4、错误提示          showError(int code ,String str)  将错误返回
       */
    public class calc {
        public calc() {
        }

        /*
                  * 计算表达式
                  * 从左向右扫描，数字入number栈，运算符入operator栈
                  * +-基本优先级为1，×÷基本优先级为2，log ln sin cos tan n!基本优先级为3，√^基本优先级为4
                  * 括号内层运算符比外层同级运算符优先级高4
                  * 当前运算符优先级高于栈顶压栈，低于栈顶弹出一个运算符与两个数进行运算
                  * 重复直到当前运算符大于栈顶
                  * 扫描完后对剩下的运算符与数字依次计算
                  */
        final int MAXLEN = 500;

        public void process(String str) {
            int weightPlus = 0, topOP = 0,
                    topNum = 0, flag = 1, weightTemp = 0;
            //weightPlus为同一（）下的基本优先级，weightTemp临时记录优先级的变化
            //topOp为weight[]，operator[]的计数器；topNum为number[]的计数器
            //flag为正负数的计数器，1为正数，-1为负数
            int weight[];//保存operator栈中运算符的优先级,以topOp计数
            double number[];//保存数字，以topNum计数
            char ch, ch_gai = 0, operator[];//operator[]保存运算符，以topOP计数
            String num = null;//记录数字，str以+-×÷()sctgl!√^分段，+-×÷()sctgl!√^字符之间的字符串即为数字
            weight = new int[MAXLEN];
            number = new double[MAXLEN];
            operator = new char[MAXLEN];
            String expression = str;
            StringTokenizer expToken = new StringTokenizer(expression);
            int i = 0;
            while (i < expression.length()) {
                ch = expression.charAt(i);

                //判断正负数
                if (i == 0) {
                    if (ch == '-') {
                        flag = -1;
                    }
                } else if (expression.charAt(i - 1) == '(' && ch == '-') {
                    flag = -1;
                }

                //取得数字，并将正负号转移给数字
                if (ch <= '9' && ch >= '0' || ch == '.' || ch == 'E') {
                    num = expToken.nextToken();//取出ch对应段的那个数字
                    ch_gai = ch;
                    //将i向后移直至ch_gai取得的字符不是数字
                    while (i < expression.length() && (ch_gai <= '9' && ch_gai >= '0' || ch_gai == '.' || ch_gai == 'E')) {
                        ch_gai = expression.charAt(i++);
                    }
                    //将ch_gai多移的位数移回来
                    if (i >= expression.length()) {
                        //说明已经取完了最后一个数字
                        i -= i;
                    } else {
                        i -= 2;
                    }
                    //如果num只取到了一个 “.”  则将其当做0处理
                    if (num.compareTo(".") == 0) {
                        number[topNum++] = 0;
                    } else {
                        //将正负号转移到数值上
                        number[topNum++] = Double.parseDouble(num) * flag;
                        flag = 1;
                    }
                }
                //计算运算符的优先级
                //先检查有没有括号，如果有括号，就应该记录下来，然后在原来的基础上加4个优先级
                if (ch == '(' || ch == ')') {
                    weightPlus += 4;
                }
                if (ch == '-' && flag == 1 || ch == '+' || ch == '×' || ch == '÷' ||
                        ch == 's' || ch == 'c' || ch == 't' || ch == 'g' || ch == 'l' ||
                        ch == '!' || ch == '√' || ch == '^') {
                    switch (ch) {
                        //+-的优先级最低，为1
                        case '+':
                        case '-':
                            weightTemp = 1 + weightPlus;
                            break;
                        //'×''÷'的优先级稍高，为2
                        case '÷':
                        case '×':
                            weightTemp = 22 + weightPlus;
                            break;
                        //sincos之类优先级为3：
                        case 's':
                        case 'c':
                        case 't':
                        case 'g':
                        case 'l':
                        case '!':
                            weightTemp = 3 + weightPlus;
                            break;
                        //其余优先级为4
                        //case '^':
                        //case '√':
                        default:
                            weightTemp = 4 + weightPlus;
                            break;
                    }
                    //如果当前优先级大于堆栈顶部元素，直接入栈
                    if (topOP == 0 || weight[topOP - 1] < weightTemp) {
                        weight[topOP] = weightTemp;
                        operator[topOP++] = ch;
                    } else {
                        //否则将堆栈中运算符逐个取出，直到当前堆栈顶部运算符优先级小于当前优先级
                        while (topOP > 0 && weight[topOP - 1] >= weightTemp) {
                            switch (operator[topOP - 1]) {
                                //取出数字数组的相应元素进行计算
                                case '+':
                                    number[topNum - 2] += number[topNum - 1];
                                    break;
                                case '-':
                                    number[topNum - 2] -= number[topNum - 1];
                                    break;
                                case '×':
                                    number[topNum - 2] *= number[topNum - 1];
                                    break;
                                case '÷':
                                    //判断除数为0的情况

                                    if (number[topNum - 1] == 0) {
                                        showError(1, str_old);
                                        return;
                                    }
                                    number[topNum - 2] /= number[topNum - 1];
                                    break;
                                case '√':
                                    if (number[topNum - 1] == 0 || (number[topNum - 2] < 0 && number[topNum - 1] % 2 == 0)) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 2] = Math.pow(number[topNum - 2], 1 / number[topNum - 1]);
                                    break;
                                case '^':
                                    number[topNum - 2] = Math.pow(number[topNum - 2], number[topNum - 1]);
                                    break;
                                //进行角度弧度判断的计算及转换
                                //sin
                                case 's':
                                    if (drg_flag == true) {
                                        number[topNum - 1] = Math.sin((number[topNum - 1] / 180) * pi);
                                    } else {
                                        number[topNum - 1] = Math.sin(number[topNum - 1]);
                                    }
                                    break;
                                case 'c':
                                    if (drg_flag == true) {
                                        number[topNum - 1] = Math.cos((number[topNum - 1] / 180) * pi);
                                    } else {
                                        number[topNum - 1] = Math.cos(number[topNum - 1]);
                                    }
                                    break;
                                case 't':
                                    if (drg_flag == true) {
                                        if ((Math.abs(number[topNum - 1]) / 90) % 2 == 1) {
                                            showError(2, str_old);
                                            return;
                                        }
                                        number[topNum - 1] = Math.tan((number[topNum - 1] / 180) * pi);
                                    } else {
                                        if ((Math.abs(number[topNum - 1]) / (pi / 2)) % 2 == 1) {
                                            showError(2, str_old);
                                            return;
                                        }
                                        number[topNum - 1] = Math.tan(number[topNum - 1]);
                                    }
                                    break;
                                case 'g':
                                    if (number[topNum - 1] <= 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = Math.log10(number[topNum - 1]);
                                    topNum++;
                                    break;
                                //ln
                                case 'l':
                                    if (number[topNum - 1] <= 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = Math.log(number[topNum - 1]);
                                    topNum++;
                                    break;
                                //阶乘
                                case '!':
                                    if (number[topNum - 1] > 170) {
                                        showError(3, str_old);
                                        return;
                                    } else if (number[topNum - 1] < 0) {
                                        showError(2, str_old);
                                        return;
                                    }
                                    number[topNum - 1] = N(number[topNum - 1]);
                                    topNum++;
                                    break;
                            }
                            //继续取堆栈中的下一个元素进行判断
                            topNum--;
                            topOP--;
                        }
                        //将运算符入堆栈
                        weight[topOP] = weightTemp;
                        operator[topOP] = ch;
                        topOP++;
                    }
                }
                i++;
            }
            //依次取出堆栈中的运算符进行运算
            while (topOP > 0) {
//+-x直接将数组的后两位数取出运算
                switch (operator[topOP - 1]) {
                    case '+':
                        number[topNum - 2] += number[topNum - 1];
                        break;
                    case '-':
                        number[topNum - 2] -= number[topNum - 1];
                        break;
                    case '×':
                        number[topNum - 2] *= number[topNum - 1];
                        break;
                    //涉及到除法时要考虑除数不能为零的情况
                    case '÷':
                        if (number[topNum - 1] == 0) {
                            showError(1, str_old);
                            return;
                        }
                        number[topNum - 2] /= number[topNum - 1];
                        break;
                    case '√':
                        if (number[topNum - 1] == 0 || (number[topNum - 2] < 0 &&
                                number[topNum - 1] % 2 == 0)) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 2] =
                                Math.pow(number[topNum - 2], 1 / number[topNum - 1]);
                        break;
                    case '^':
                        number[topNum - 2] =
                                Math.pow(number[topNum - 2], number[topNum - 1]);
                        break;
                    //sin
                    case 's':
                        if (drg_flag == true) {
                            number[topNum - 1] = Math.sin((number[topNum - 1] / 180) * pi);
                        } else {
                            number[topNum - 1] = Math.sin(number[topNum - 1]);
                        }
                        topNum++;
                        break;
                    //cos
                    case 'c':
                        if (drg_flag == true) {
                            number[topNum - 1] = Math.cos((number[topNum - 1] / 180) * pi);
                        } else {
                            number[topNum - 1] = Math.cos(number[topNum - 1]);
                        }
                        topNum++;
                        break;
                    //tan
                    case 't':
                        if (drg_flag == true) {
                            if ((Math.abs(number[topNum - 1]) / 90) % 2 == 1) {
                                showError(2, str_old);
                                return;
                            }
                            number[topNum - 1] = Math.tan((number[topNum - 1] / 180) * pi);
                        } else {
                            if ((Math.abs(number[topNum - 1]) / (pi / 2)) % 2 == 1) {
                                showError(2, str_old);
                                return;
                            }
                            number[topNum - 1] = Math.tan(number[topNum - 1]);
                        }
                        topNum++;
                        break;
                    //对数log
                    case 'g':
                        if (number[topNum - 1] <= 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = Math.log10(number[topNum - 1]);
                        topNum++;
                        break;
                    //自然对数ln
                    case 'l':
                        if (number[topNum - 1] <= 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = Math.log(number[topNum - 1]);
                        topNum++;
                        break;
                    //阶乘
                    case '!':
                        if (number[topNum - 1] > 170) {
                            showError(3, str_old);
                            return;
                        } else if (number[topNum - 1] < 0) {
                            showError(2, str_old);
                            return;
                        }
                        number[topNum - 1] = N(number[topNum - 1]);
                        topNum++;
                        break;
                }
                //取堆栈下一个元素计算
                topNum--;
                topOP--;
            }
            //如果数字太大，就提示错误信息
            if (number[0] > 7.3E306) {
                showError(3, str_old);
                return;
            }
            //输出最终结果
            input.setText(String.valueOf(FP(number[0])));
            tip.setText("计算完毕，要继续请按归零见C");
            mem.setText(str_old + "=" + String.valueOf(FP(number[0])));
        }

        private void showError(int code, String str) {
            String message = "";
            switch (code) {
                case 1:
                    message = "零不能作除数";
                    break;
                case 2:
                    message = "函数格式错误";
                    break;
                case 3:
                    message = "值太大了，超出范围";
            }
            input.setText("\"" + str + "\"" + ": " + message);
            tip.setText(message + "\n" + "计算完毕，要继续请按归零键 C");
        }

    }

    /*
           * FP = floating point 控制小数位数，达到精度
           * 否则会出现 0.6-0.2=0.39999999999999997的情况，用FP即可解决，使得数为0.4
           * 本格式精度为15位
           */
    public double FP(double n) {
        //NumberFormat format=NumberFormat.getInstance();  //创建一个格式化类f
        //format.setMaximumFractionDigits(18);    //设置小数位的格式
        DecimalFormat format = new DecimalFormat("0.#############");
        return Double.parseDouble(format.format(n));
    }


    private double N(double n) {
        int i = 0;
        double sum = 1;
        //依次将小于等于n的值相乘
        for (i = 1; i <= n; i++) {
            sum = sum * i;
        }
        return sum;
    }
}