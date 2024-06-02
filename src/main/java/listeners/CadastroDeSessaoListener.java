package listeners;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CadastroDeSessaoListener extends ListenerAdapter {

    private final Map<String, LocalDate> cache = new ConcurrentHashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("cadastrar-horario")) {
            var ano = TextInput.create("ano", "Ano", TextInputStyle.SHORT)
                    .setPlaceholder("Mantenha em branco para usar o ano atual")
                    .setRequiredRange(4, 4)
                    .setRequired(false)
                    .build();

            var mes = TextInput.create("mes", "Mês", TextInputStyle.SHORT)
                    .setPlaceholder("Mantenha em branco para usar o mês atual")
                    .setRequired(false)
                    .build();

            var dia = TextInput.create("dia", "Dia", TextInputStyle.SHORT)
                    .setRequiredRange(1, 2)
                    .setRequired(true)
                    .build();

            var modal = Modal.create("modal-cadastro-sessao", "Cadastro de sessão")
                    .addActionRows(ActionRow.of(ano), ActionRow.of(mes), ActionRow.of(dia))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getInteraction().getModalId().equals("modal-cadastro-sessao")) {
            var dataAtual = LocalDate.now();

            var valores = event.getInteraction().getValues()
                    .stream()
                    .collect(Collectors.toMap(m -> m.getId(), m -> m.getAsString()));

            var ano = valores.get("ano").isBlank() ? dataAtual.getYear() : Integer.parseInt(valores.get("ano"));
            var mes = valores.get("mes").isBlank() ? dataAtual.getMonth() : Month.of(Integer.parseInt(valores.get("mes")));
            var dia = Integer.parseInt(valores.get("dia"));
            var userId = event.getInteraction().getUser().getId();
            var data = LocalDate.of(ano, mes, dia);

            cache.put(userId, data);

            var selectMenuPeriodo = StringSelectMenu.create("selecao-periodo")
                    .addOption("Manhã", "manha")
                    .addOption("Tarde", "tarde")
                    .addOption("Noite", "noite")
                    .build();

            event
                    .replyComponents(ActionRow.of(selectMenuPeriodo))
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("selecao-periodo")) {
            var periodo = event.getInteraction().getValues().getFirst();
            var userId = event.getInteraction().getUser().getId();
            var username = event.getJDA().getUserById(userId).getName();
            var data = cache.get(userId);
            cache.remove(userId);

            var mensagem = String.format("Foi selecionado %s - %s - por : %s", data.toString(), periodo, username);


            event.reply(mensagem).setEphemeral(true).queue();
        }
    }
}
