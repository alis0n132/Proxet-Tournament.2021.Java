package proxet.tournament.generator;

import proxet.tournament.generator.dto.Player;
import proxet.tournament.generator.dto.TeamGeneratorResult;

import java.io.File;
import java.util.*;

import static java.lang.Integer.parseInt;

public class TeamGenerator {

    private static final int INITIAL_CAPACITY = 300000;
    private static final int PLAYERS_PER_TEAM = 9;

    public TeamGeneratorResult generateTeams(String filePath) {

        PriorityQueue<WaitingPlayer> playersOnFirstVehicle = new PriorityQueue<>(INITIAL_CAPACITY);
        PriorityQueue<WaitingPlayer> playersOnSecondVehicle = new PriorityQueue<>(INITIAL_CAPACITY);
        PriorityQueue<WaitingPlayer> playersOnThirdVehicle = new PriorityQueue<>(INITIAL_CAPACITY);

        try (Scanner scanner = new Scanner(new File(filePath))) {
            parseWaitingPlayers(playersOnFirstVehicle, playersOnSecondVehicle, playersOnThirdVehicle, scanner);
        } catch (Exception e) {
            return new TeamGeneratorResult(Collections.emptyList(), Collections.emptyList());
        }

        ArrayList<Player> firstTeam = new ArrayList<>();
        ArrayList<Player> secondTeam = new ArrayList<>();

        for (int i = 0; i < PLAYERS_PER_TEAM / 3; i++) {
            fillWithTop3Waiters(playersOnFirstVehicle, playersOnSecondVehicle, playersOnThirdVehicle, firstTeam);
            fillWithTop3Waiters(playersOnFirstVehicle, playersOnSecondVehicle, playersOnThirdVehicle, secondTeam);
        }

        return new TeamGeneratorResult(firstTeam, secondTeam);
    }

    private void parseWaitingPlayers(PriorityQueue<WaitingPlayer> vehicle1Players,
                                     PriorityQueue<WaitingPlayer> vehicle2Players,
                                     PriorityQueue<WaitingPlayer> vehicle3Players,
                                     Scanner scanner) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] strPlayer = line.split("\\s+");
            WaitingPlayer player = new WaitingPlayer(strPlayer[0], parseInt(strPlayer[2]), parseInt(strPlayer[1]));

            if (player.vehicleType == 1) {
                vehicle1Players.add(player);
            } else if (player.vehicleType == 2) {
                vehicle2Players.add(player);
            } else {
                vehicle3Players.add(player);
            }
        }
    }

    private void fillWithTop3Waiters(PriorityQueue<WaitingPlayer> vehicle1Players,
                                     PriorityQueue<WaitingPlayer> vehicle2Players,
                                     PriorityQueue<WaitingPlayer> vehicle3Players, ArrayList<Player> team) {
        WaitingPlayer vehicle1Player = vehicle1Players.poll();
        team.add(new Player(Objects.requireNonNull(vehicle1Player).nickname, vehicle1Player.vehicleType));

        WaitingPlayer vehicle2Player = vehicle2Players.poll();
        team.add(new Player(Objects.requireNonNull(vehicle2Player).nickname, vehicle2Player.vehicleType));

        WaitingPlayer vehicle3Player = vehicle3Players.poll();
        team.add(new Player(Objects.requireNonNull(vehicle3Player).nickname, vehicle3Player.vehicleType));
    }

    private static class WaitingPlayer implements Comparable<WaitingPlayer> {
        private final String nickname;
        private final int vehicleType;
        private final int waitingTime;

        public WaitingPlayer(String nickname, int vehicleType, int waitingTime) {
            this.nickname = nickname;
            this.vehicleType = vehicleType;
            this.waitingTime = waitingTime;
        }

        @Override
        public int compareTo(WaitingPlayer o) {
            return o.waitingTime - this.waitingTime; //desc order
        }
    }
}

