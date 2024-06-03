package listeners;

import model.Sessao;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.types.ObjectId;
import service.SessaoService;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class MarcarSessaoListener extends ListenerAdapter {

    private final static long ROLE_ID_JOGADOR = 1211055170079686656l;
    private final static long ROLE_ID_MESTRE = 1211055645000732752l;
    private final static long ID_GUILD = 1211053454139854848l;
    private final static String SELECAO_MESTRE_EVENT_UID = "selecao_mestre_event_";
    private final static Pattern PADRAO_SELECAO_MESTRE_EVENT = Pattern.compile(SELECAO_MESTRE_EVENT_UID + "(.*)");
    private final static String SELECAO_SESSAO_EVENT_UID = "selecao_sessao_event_";
    private final static Pattern PADRAO_SELECAO_SESSAO_EVENT = Pattern.compile(SELECAO_SESSAO_EVENT_UID + "(.*)");

    private final SessaoService sessaoService;

    public MarcarSessaoListener(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        var isComandoMarcarSessao = event.getName().equals("marcar-sessao-com-narrador");

        if (isComandoMarcarSessao) {
            var jda = event.getJDA();
            var guild = jda.getGuildById(ID_GUILD);
            var roleJogador = guild.getRoleById(ROLE_ID_JOGADOR);

            guild.findMembersWithRoles(roleJogador).onSuccess(members -> {
                var possuiMembro = members
                        .stream()
                        .anyMatch(member -> member.getIdLong() == event.getUser().getIdLong());

                if (possuiMembro) {
                    var roleMestre = guild.getRoleById(ROLE_ID_MESTRE);

                    guild.findMembersWithRoles(roleMestre).onSuccess(mestres -> {
                        if (mestres.isEmpty()) {
                            event.reply("Estranho, não existem mestres");
                        } else {
                            var compomentesBotoesSelecaoMestre = mestres
                                    .stream()
                                    .map(mestre ->
                                            ActionRow.of(Button.primary(
                                                    SELECAO_MESTRE_EVENT_UID + mestre.getId(),
                                                    mestre.getEffectiveName())))
                                    .toList();

                            event.replyComponents(compomentesBotoesSelecaoMestre).setEphemeral(true).queue();
                        }
                    });
                } else {
                    event.reply("Você não possui a role de jogador na Hells Paradise, solicite para um dos mestres").queue();
                }
            });
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        var matcherSelecaoMestre = PADRAO_SELECAO_MESTRE_EVENT.matcher(event.getButton().getId());
        var matcherSelecaoSessao = PADRAO_SELECAO_SESSAO_EVENT.matcher(event.getButton().getId());

        if (matcherSelecaoMestre.find()) {
            var dataAtual = LocalDate.now();
            var idMestre = Long.valueOf(matcherSelecaoMestre.group(1));
            var sessoes = sessaoService.buscarSessoesLivres(idMestre, dataAtual, dataAtual.plusDays(7));

            if (sessoes.isEmpty()) {
                event.reply("Esse mestre não possui sessões livres cadastradas").queue();
            } else {
                var componentesBotoesSelecaoSessao = sessoes
                        .stream()
                        .map(sessao ->
                                ActionRow.of(Button.primary(
                                        SELECAO_SESSAO_EVENT_UID + sessao.getId().toHexString(),
                                        formatarDescricaoSessao(sessao))))
                        .toList();

                event.replyComponents(componentesBotoesSelecaoSessao).queue();
            }
        } else if (matcherSelecaoSessao.find()) {
            var idSessao = new ObjectId(matcherSelecaoSessao.group(1));
            var sessao = sessaoService.buscarSessao(idSessao).get();
            var idJogador = event.getUser().getIdLong();

            sessao.adicionarJogador(idJogador);

            sessaoService.atualizar(sessao);

            event
                    .reply("Sessão agendada com sucesso !!! \n Notificando o mestre responsável...")
                    .onSuccess((ignore) -> {
                        var nomeJogador = event.getUser().getName();
                        var idMestre = sessao.getMestreId();

                        event
                                .getJDA()
                                .retrieveUserById(idMestre)
                                .map(User::openPrivateChannel)
                                .flatMap(channel -> channel)
                                .queue(channel ->
                                        channel.sendMessage(
                                                        String.format("Olá o jogador %s acabou de marcar uma sessao com você as %s no periodo %s",
                                                                nomeJogador,
                                                                sessao.getData(),
                                                                sessao.getPeriodo().descricao))
                                                .queue()
                                );
                    }).queue();
        }
    }

    private String formatarDescricaoSessao(Sessao sessao) {
        return sessao.getData() + " " + sessao.getPeriodo().descricao;
    }
}
