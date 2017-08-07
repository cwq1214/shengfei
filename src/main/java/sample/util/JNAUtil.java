package sample.util;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Created by Bee on 2017/7/25.
 */
public class JNAUtil {
    public interface User32 extends StdCallLibrary
    {
        User32 INSTANCE = (User32) Native.loadLibrary("User32",User32.class);//加载系统User32 DLL文件，也可以是C++写的DLL文件
//        User32 INSTANCE = (User32) Native.loadLibrary("C:\\Windows\\System32\\user32.dll",User32.class);//加载系统User32 DLL文件，也可以是C++写的DLL文件
        int SendMessageA(int hwnd,int msg,int wparam,int lparam);
        int FindWindowA(String arg0,String arg1);
        void BlockInput(boolean isBlock);
        int MessageBoxA(int hWnd,String lpText,int lpCaption,int uType);
        int SetParent(int hWnd,int parentHwnd);
        int MoveWindow(int hWnd,int x,int y,int width,int height,boolean brepeat);
        boolean ShowWindow(int hwnd,int type);
        int CloseWindow(int hwnd);
    }

    public static int hwndWithName(String className,String name){
        return User32.INSTANCE.FindWindowA(className,name);
    }

    public static int sendMessageWithHwnd(int hwnd,int msg,int wparam,int lparam){
        return User32.INSTANCE.SendMessageA(hwnd,msg,wparam,lparam);
    }

    public static int setParent(int hwnd,int parentHwnd){
        return User32.INSTANCE.SetParent(hwnd,parentHwnd);
    }

    public static int moveWindow(int hWnd,int x,int y,int width,int height,boolean brepeat){
        return User32.INSTANCE.MoveWindow(hWnd,x,y,width,height,brepeat);
    }

    public  static boolean showWindow(int hwnd,int type){
        return User32.INSTANCE.ShowWindow(hwnd,type);
    }

    public static int closeWindow(int hwnd){
        return User32.INSTANCE.CloseWindow(hwnd);
    }
}
