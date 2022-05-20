package org.scrum.psd.battleship.ascii;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import org.scrum.psd.battleship.controller.GameController;
import org.scrum.psd.battleship.controller.dto.Letter;
import org.scrum.psd.battleship.controller.dto.Position;
import org.scrum.psd.battleship.controller.dto.Ship;
import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static com.diogonunes.jcolor.Ansi.colorize;
import static com.diogonunes.jcolor.Attribute.*;

public class Main {
    private static List<Ship> myFleet;
    private static List<Ship> enemyFleet;

    public static void main(String[] args) {

        if(System.getProperty("os.name").startsWith("Windows"))
        {
            // Set output mode to handle virtual terminal sequences
            Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
            DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
            HANDLE hOut = (HANDLE)GetStdHandleFunc.invoke(HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

            DWORDByReference p_dwMode = new DWORDByReference(new DWORD(0));
            Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
            GetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, p_dwMode});

            int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
            DWORD dwMode = p_dwMode.getValue();
            dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
            Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
            SetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, dwMode});
        }

        System.out.println(colorize("                                     |__", MAGENTA_TEXT()));
        System.out.println(colorize("                                     |\\/", MAGENTA_TEXT()));
        System.out.println(colorize("                                     ---", MAGENTA_TEXT()));
        System.out.println(colorize("                                     / | [", MAGENTA_TEXT()));
        System.out.println(colorize("                              !      | |||", MAGENTA_TEXT()));
        System.out.println(colorize("                            _/|     _/|-++'", MAGENTA_TEXT()));
        System.out.println(colorize("                        +  +--|    |--|--|_ |-", MAGENTA_TEXT()));
        System.out.println(colorize("                     { /|__|  |/\\__|  |--- |||__/", MAGENTA_TEXT()));
        System.out.println(colorize("                    +---------------___[}-_===_.'____                 /\\", MAGENTA_TEXT()));
        System.out.println(colorize("                ____`-' ||___-{]_| _[}-  |     |_[___\\==--            \\/   _", MAGENTA_TEXT()));
        System.out.println(colorize(" __..._____--==/___]_|__|_____________________________[___\\==--____,------' .7", MAGENTA_TEXT()));
        System.out.println(colorize("|                        Welcome to Battleship                         BB-61/", MAGENTA_TEXT()));
        System.out.println(colorize(" \\_________________________________________________________________________|", MAGENTA_TEXT()));
        System.out.println("");

        InitializeGame();

        StartGame();
    }

    private static void StartGame() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\033[2J\033[;H");
        System.out.println("                  __");
        System.out.println("                 /  \\");
        System.out.println("           .-.  |    |");
        System.out.println("   *    _.-'  \\  \\__/");
        System.out.println("    \\.-'       \\");
        System.out.println("   /          _/");
        System.out.println("  |      _  /\" \"");
        System.out.println("  |     /_\'");
        System.out.println("   \\    \\_/");
        System.out.println("    \" \"\" \"\" \"\" \"");

        int i = 1;
        do {
            System.out.println("");
            System.out.printf("------- Round %d --------" + System.lineSeparator(), i);
            System.out.println("Player, it's your turn");
            System.out.println("Enter coordinates for your shot :");
            Position position = parsePosition(scanner.next());
            boolean isHit = GameController.checkIsHit(enemyFleet, position);
            if (isHit) {
                beep();

                System.out.println("                \\         .  ./");
                System.out.println("              \\      .:\" \";'.:..\" \"   /");
                System.out.println("                  (M^^.^~~:.'\" \").");
                System.out.println("            -   (/  .    . . \\ \\)  -");
                System.out.println("               ((| :. ~ ^  :. .|))");
                System.out.println("            -   (\\- |  \\ /  |  /)  -");
                System.out.println("                 -\\  \\     /  /-");
                System.out.println("                   \\  \\   /  /");
            }

            System.out.println(isHit ? colorize("Yeah ! Nice hit !", YELLOW_TEXT()) : colorize("Miss", BLUE_TEXT()));

            position = getRandomPosition();
            isHit = GameController.checkIsHit(myFleet, position);
            System.out.println("");
            System.out.println(String.format(colorize("Computer shoot in %s%s and %s", CYAN_TEXT()), position.getColumn(), position.getRow(), isHit ? colorize("hit your ship !", YELLOW_TEXT()) : colorize("miss", BLUE_TEXT())));
            if (isHit) {
                beep();

                System.out.println("                \\         .  ./");
                System.out.println("              \\      .:\" \";'.:..\" \"   /");
                System.out.println("                  (M^^.^~~:.'\" \").");
                System.out.println("            -   (/  .    . . \\ \\)  -");
                System.out.println("               ((| :. ~ ^  :. .|))");
                System.out.println("            -   (\\- |  \\ /  |  /)  -");
                System.out.println("                 -\\  \\     /  /-");
                System.out.println("                   \\  \\   /  /");

            }

            Ship notSunkEnemyShip =
            enemyFleet
                    .stream()
                    .filter(e -> e.isSunk().equals(Boolean.FALSE))
                    .findAny()
                    .orElse(null);

            i++;
        } while (true);
    }

    private static void beep() {
        System.out.print("\007");
    }

    protected static Position parsePosition(String input) {
        Letter letter = Letter.valueOf(input.toUpperCase().substring(0, 1));
        int number = Integer.parseInt(input.substring(1));
        return new Position(letter, number);
    }

    private static Position getRandomPosition() {
        int rows = 8;
        int lines = 8;
        Random random = new Random();
        Letter letter = Letter.values()[random.nextInt(lines)];
        int number = random.nextInt(rows);
        Position position = new Position(letter, number);
        return position;
    }

    private static void InitializeGame() {
        InitializeMyFleet();

        InitializeEnemyFleet();
    }

    private static void InitializeMyFleet() {
        Scanner scanner = new Scanner(System.in);
        myFleet = GameController.initializeShips();

        System.out.println("Please position your fleet (Game board has size from A to H and 1 to 8) :");

        for (Ship ship : myFleet) {
            System.out.println("");
            System.out.println(String.format("Please enter the positions for the %s (size: %s)", ship.getName(), ship.getSize()));
            for (int i = 1; i <= ship.getSize(); i++) {
                System.out.println(String.format("Enter position %s of %s (i.e A3):", i, ship.getSize()));

                String positionInput = scanner.next();
                ship.addPosition(positionInput);
            }
        }
    }

    private static void InitializeEnemyFleet() {
        enemyFleet = GameController.initializeShips();

        int version = getRandomNumber(0, 4);

        switch (version) {
        case 0:
            enemyFleet.get(0).getPositions().add(new Position(Letter.B, 4));
            enemyFleet.get(0).getPositions().add(new Position(Letter.B, 5));
            enemyFleet.get(0).getPositions().add(new Position(Letter.B, 6));
            enemyFleet.get(0).getPositions().add(new Position(Letter.B, 7));
            enemyFleet.get(0).getPositions().add(new Position(Letter.B, 8));

            enemyFleet.get(1).getPositions().add(new Position(Letter.E, 5));
            enemyFleet.get(1).getPositions().add(new Position(Letter.E, 6));
            enemyFleet.get(1).getPositions().add(new Position(Letter.E, 7));
            enemyFleet.get(1).getPositions().add(new Position(Letter.E, 8));

            enemyFleet.get(2).getPositions().add(new Position(Letter.A, 3));
            enemyFleet.get(2).getPositions().add(new Position(Letter.B, 3));
            enemyFleet.get(2).getPositions().add(new Position(Letter.C, 3));

            enemyFleet.get(3).getPositions().add(new Position(Letter.F, 8));
            enemyFleet.get(3).getPositions().add(new Position(Letter.G, 8));
            enemyFleet.get(3).getPositions().add(new Position(Letter.H, 8));

            enemyFleet.get(4).getPositions().add(new Position(Letter.C, 5));
            enemyFleet.get(4).getPositions().add(new Position(Letter.C, 6));
            break;

        case 1:
            enemyFleet.get(0).getPositions().add(new Position(Letter.C, 5));
            enemyFleet.get(0).getPositions().add(new Position(Letter.D, 5));
            enemyFleet.get(0).getPositions().add(new Position(Letter.E, 5));
            enemyFleet.get(0).getPositions().add(new Position(Letter.F, 5));
            enemyFleet.get(0).getPositions().add(new Position(Letter.G, 5));

            enemyFleet.get(1).getPositions().add(new Position(Letter.B, 1));
            enemyFleet.get(1).getPositions().add(new Position(Letter.B, 2));
            enemyFleet.get(1).getPositions().add(new Position(Letter.B, 3));
            enemyFleet.get(1).getPositions().add(new Position(Letter.B, 4));

            enemyFleet.get(2).getPositions().add(new Position(Letter.B, 7));
            enemyFleet.get(2).getPositions().add(new Position(Letter.C, 7));
            enemyFleet.get(2).getPositions().add(new Position(Letter.D, 7));

            enemyFleet.get(3).getPositions().add(new Position(Letter.F, 1));
            enemyFleet.get(3).getPositions().add(new Position(Letter.F, 2));
            enemyFleet.get(3).getPositions().add(new Position(Letter.F, 3));

            enemyFleet.get(4).getPositions().add(new Position(Letter.D, 2));
            enemyFleet.get(4).getPositions().add(new Position(Letter.D, 3));
            break;

        case 2:
            enemyFleet.get(0).getPositions().add(new Position(Letter.A, 2));
            enemyFleet.get(0).getPositions().add(new Position(Letter.A, 3));
            enemyFleet.get(0).getPositions().add(new Position(Letter.A, 4));
            enemyFleet.get(0).getPositions().add(new Position(Letter.A, 5));
            enemyFleet.get(0).getPositions().add(new Position(Letter.A, 6));

            enemyFleet.get(1).getPositions().add(new Position(Letter.G, 4));
            enemyFleet.get(1).getPositions().add(new Position(Letter.G, 5));
            enemyFleet.get(1).getPositions().add(new Position(Letter.G, 6));
            enemyFleet.get(1).getPositions().add(new Position(Letter.G, 7));

            enemyFleet.get(2).getPositions().add(new Position(Letter.B, 3));
            enemyFleet.get(2).getPositions().add(new Position(Letter.C, 3));
            enemyFleet.get(2).getPositions().add(new Position(Letter.D, 3));

            enemyFleet.get(3).getPositions().add(new Position(Letter.F, 8));
            enemyFleet.get(3).getPositions().add(new Position(Letter.G, 8));
            enemyFleet.get(3).getPositions().add(new Position(Letter.H, 8));

            enemyFleet.get(4).getPositions().add(new Position(Letter.C, 5));
            enemyFleet.get(4).getPositions().add(new Position(Letter.C, 6));
            break;

        case 3:
            enemyFleet.get(0).getPositions().add(new Position(Letter.H, 2));
            enemyFleet.get(0).getPositions().add(new Position(Letter.H, 3));
            enemyFleet.get(0).getPositions().add(new Position(Letter.H, 4));
            enemyFleet.get(0).getPositions().add(new Position(Letter.H, 5));
            enemyFleet.get(0).getPositions().add(new Position(Letter.H, 6));

            enemyFleet.get(1).getPositions().add(new Position(Letter.E, 8));
            enemyFleet.get(1).getPositions().add(new Position(Letter.F, 8));
            enemyFleet.get(1).getPositions().add(new Position(Letter.G, 8));
            enemyFleet.get(1).getPositions().add(new Position(Letter.H, 8));

            enemyFleet.get(2).getPositions().add(new Position(Letter.A, 8));
            enemyFleet.get(2).getPositions().add(new Position(Letter.B, 8));
            enemyFleet.get(2).getPositions().add(new Position(Letter.C, 8));

            enemyFleet.get(3).getPositions().add(new Position(Letter.A, 4));
            enemyFleet.get(3).getPositions().add(new Position(Letter.A, 5));
            enemyFleet.get(3).getPositions().add(new Position(Letter.A, 6));

            enemyFleet.get(4).getPositions().add(new Position(Letter.D, 1));
            enemyFleet.get(4).getPositions().add(new Position(Letter.E, 1));
            break;

        case 4:
            enemyFleet.get(0).getPositions().add(new Position(Letter.D, 2));
            enemyFleet.get(0).getPositions().add(new Position(Letter.D, 3));
            enemyFleet.get(0).getPositions().add(new Position(Letter.D, 4));
            enemyFleet.get(0).getPositions().add(new Position(Letter.D, 5));
            enemyFleet.get(0).getPositions().add(new Position(Letter.D, 6));

            enemyFleet.get(1).getPositions().add(new Position(Letter.G, 3));
            enemyFleet.get(1).getPositions().add(new Position(Letter.G, 4));
            enemyFleet.get(1).getPositions().add(new Position(Letter.G, 5));
            enemyFleet.get(1).getPositions().add(new Position(Letter.G, 6));

            enemyFleet.get(2).getPositions().add(new Position(Letter.B, 3));
            enemyFleet.get(2).getPositions().add(new Position(Letter.C, 3));
            enemyFleet.get(2).getPositions().add(new Position(Letter.D, 3));

            enemyFleet.get(3).getPositions().add(new Position(Letter.F, 8));
            enemyFleet.get(3).getPositions().add(new Position(Letter.G, 8));
            enemyFleet.get(3).getPositions().add(new Position(Letter.H, 8));

            enemyFleet.get(4).getPositions().add(new Position(Letter.C, 5));
            enemyFleet.get(4).getPositions().add(new Position(Letter.C, 6));
            break;
        }
    }

    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
