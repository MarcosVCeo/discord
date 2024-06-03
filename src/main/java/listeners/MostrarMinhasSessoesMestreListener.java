package listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import service.SessaoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MostrarMinhasSessoesMestreListener extends ListenerAdapter {

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final SessaoService sessaoService;

    public MostrarMinhasSessoesMestreListener(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("mostrar-meus-horarios")) {
            var dataAtual = LocalDate.now();
            var idMestre = Long.valueOf(event.getUser().getId());

            var sessoes = sessaoService.buscarSessoes(idMestre, dataAtual, dataAtual.plusDays(7));

            if (sessoes.isEmpty()) {
                event.reply("Você não possui sessoes cadastradas no periodo de uma semana").queue();
            } else {
                var sbMensagem = new StringBuilder();

                sessoes.forEach(sessao -> {
                    sbMensagem
                            .append("Sessao {")
                            .append("data : ")
                            .append(fmt.format(sessao.getData()))
                            .append(" | Periodo : ")
                            .append(String.format("%6s", sessao.getPeriodo().descricao))
                            .append(" | jogadores : { ");

                    if (!sessao.getJogadoresIds().isEmpty()) {

                        sessao.getJogadoresIds()
                                .stream()
                                .map(id -> event.getJDA().retrieveUserById(id).map(User::getEffectiveName).complete())
                                .forEach(nome -> sbMensagem.append(nome).append(" "));
                    }

                    sbMensagem.append("}\n");
                });

                event.reply(sbMensagem.toString()).setEphemeral(true).queue();
            }
        }
    }
}
