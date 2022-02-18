<div align="center">
<br/>
  <p>
    <img src="https://img.shields.io/static/v1?label=Version&message=Alpha-0.2.2&color=12c970&logoColor=white" alt="Version" />
	<br>
	</p>
  </p>
</div>

# TerminalCore
Terminal GUI library for Windows/Linux.

This library contains all colors as ascii codes, native functions of the respective operating systems, cursor movement and simplified terminal functions.

Documentation: https://apicodeyt.github.io/TerminalCore/

![Sample app](https://github.com/APICodeYT/TerminalCore/blob/main/ressource-assets/2022-02-17-13-19-42.gif)



### Example (Simple Terminal)
```java

Terminal t = new Terminal();
t.setTitle("Test!");
t.clearScreen();
TStringBuilder sb = new TStringBuilder()
        .foreground(ForegroundColor.getColorFrom256ColorSet(135)) //Purple
        .append("A Terminal Test")
        .reset()
        .nextLine()
        .background(BackgroundColor.WHITE)
        .foreground(ForegroundColor.BLACK)
        .append("Background!")
        .reset();
t.writeLine(sb.toString() + "\n");
//yellow, pink
t.setColor(ForegroundColor.getColorFrom256ColorSet(227), BackgroundColor.getColorFrom256ColorSet(161));
t.writeLine("Background");
t.reset();
t.write("Press TAB to continue ...");
while (t.getch() != 9) {
    //tab not pressed
}
//tab pressed
t.write("\n\nSimple Movement:");
t.moveConsoleCursor(MoveDirection.RIGHT, 4);
t.write(ForegroundColor.RGB_AQUA + "Cursor Here!");
//wait of button
t.getch();
//close
t.dispose();

```

So can be screens used in a TerminalApplication. These screens are graphical interfaces with which you can add various components.
In addition, there are different layout types. The Absolute layout is simple and does not support any resizing of the terminal.
 Relative layouts can handle this instead and can move or resize the components when the screen is resized. The components can also be fixed so that this change is ignored.
In addition, you can also add your own RenderHandler, with which you can use this resizing in detail.

#### Important to use
For the execution the library file must be in the same folder where the program resides and must be named exactly. For windows terminal32.dll and for linux terminal.so .
To change the filename there are the public files in the TerminalHandle class. This should take place absolutely before initializing the terminal object! 


### Example (Screen Application)
```java

Terminal terminal = new Terminal(true); //enable utf8 for screen using  <= IMPORTANT!
TerminalApplication application = new TerminalApplication(terminal);
TerminalScreen screen = new TerminalScreen(application);
TLabel label = new TLabel("Button: ");
TButton button = new TButton("Click me!");
button.setClickEvent(new Runnable() {
    @Override
    public void run() {
        //action
    }
}); 
screen.addComponent(label, new TVector(2, 2)); //component, position
screen.addComponent(button, new TVector(2, 3));
        
application.openScreen(screen); //open screen
```

### 8-Bit Colorlist
![Colors](https://github.com/APICodeYT/TerminalCore/blob/main/ressource-assets/8bit-colors.jpg)

###Maven Dependency
```xml
<dependency>
    <groupId>net.pascal</groupId>
    <artifactId>terminalcore</artifactId>
    <version>VERSION</version>
</dependency>


```




