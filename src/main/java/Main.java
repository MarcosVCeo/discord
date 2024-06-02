import listeners.CadastroDeSessaoListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Main {

    private static final String DISCORD_ENV_TOKEN = "DISCORD_BOT_TOKEN";

    public static void main(String[] args) {
        final var jda = JDABuilder
                .createDefault(System.getenv(DISCORD_ENV_TOKEN))
                .addEventListeners(new CadastroDeSessaoListener())
                .build();

        jda.updateCommands()
                .addCommands(
                        Commands.slash("help", "Mostra os comandos disponivels")
                                .setGuildOnly(true)
                                .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                        Commands.slash("cadastrar-horario", "Usado para os mestres cadastrarem horários")
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_PERMISSIONS)))
                .queue();
    }
}
