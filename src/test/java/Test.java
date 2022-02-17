import net.pascal.terminal.component.LineTextBuffer;
import net.pascal.terminal.component.ScrollbarBuilder;

public class Test {

    public static void main1(String[] args) {

        LineTextBuffer buffer = new LineTextBuffer();
        buffer.setLineBufferLength(16);
        buffer.setLine(1, "Hello World!");
        buffer.setLine(2, "nice!");
        buffer.setLine(4, "Nix");
        buffer.setLine(5, "lol");
        buffer.setLine(9, "123456789123456789999");
        buffer.setCurrentPointer(5);
        buffer.pointerNewLine();
        buffer.setCurrentLine(10);
        buffer.setCurrentPointer(4);
        buffer.pointerRemoveLine();
        buffer.setCurrentLine(6);
        buffer.pointerRemoveLine();

        int i = 1;
        for(String line : buffer.lines()) {
            System.out.println(i + " | " + line);
            i++;
        }
        System.out.println(buffer.toPointerPosition().toString());
    }

    public static void main(String[] args) {
        int total = 10;

        for(int c = 1;c<10;c++) {
            char[] chars = ScrollbarBuilder.buildHorizontalScrollbar(5, c, total);
            for(char l : chars) {
                System.out.println(l);
            }
        }


    }

}
