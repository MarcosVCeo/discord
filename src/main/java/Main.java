import com.mongodb.client.MongoClients;
import listeners.CadastroDeSessaoListener;
import listeners.MarcarSessaoListener;
import listeners.MostrarMinhasSessoesMestreListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import repository.SessaoMongoRepository;
import service.SessaoService;

public class Main {

    private static final String DISCORD_ENV_TOKEN = "DISCORD_BOT_TOKEN";
    private static final String MONGO_CLIENT_ENV_URL = "MONGO_DISCORD_URL";

    public static void main(String[] args) {
        final var mongoClient = MongoClients.create(System.getenv(MONGO_CLIENT_ENV_URL));
        final var sessaoService = new SessaoService(new SessaoMongoRepository(mongoClient));

        final var jda = JDABuilder
                .createDefault(System.getenv(DISCORD_ENV_TOKEN), GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(
                        new CadastroDeSessaoListener(sessaoService),
                        new MostrarMinhasSessoesMestreListener(sessaoService),
                        new MarcarSessaoListener(sessaoService))
                .build();

        jda.updateCommands()
                .addCommands(
                        Commands.slash("help", "Mostra os comandos disponivels")
                                .setGuildOnly(true)
                                .setDefaultPermissions(DefaultMemberPermissions.ENABLED),
                        Commands.slash(
                                        "cadastrar-horario",
                                        "Usado para os mestres cadastrarem horários")
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_PERMISSIONS)),
                        Commands.slash(
                                        "mostrar-meus-horarios",
                                        "Usado para os mestres verem seus hórarios de sessão cadastrados")
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_PERMISSIONS)),
                        Commands.slash(
                                        "marcar-sessao-com-narrador",
                                        "Usado por jogadores para marcar uma sessao já cadastrada com mestres")
                                .setDefaultPermissions(DefaultMemberPermissions.ENABLED))
                .queue();
    }
}
