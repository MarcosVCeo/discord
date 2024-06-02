package repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import model.Periodo;
import model.Sessao;
import org.bson.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SessaoMongoRepository implements SessaoRepository {

    private final MongoCollection<Document> sessoesCollection;

    public SessaoMongoRepository(MongoClient mongoClient) {
        this.sessoesCollection = mongoClient
                .getDatabase("discord")
                .getCollection("sessoes");
    }

    @Override
    public Optional<Sessao> buscarSessao(Long idMestre, LocalDate data, Periodo periodo) {
        var resultado = sessoesCollection
                .find(Filters.and(
                        Filters.eq("idMestre", idMestre),
                        Filters.gte("data", data),
                        Filters.eq("periodo", periodo.descricao)
                )).first();

        if (resultado != null) {
            var sessao = documento2Sessao(resultado);

            return Optional.of(sessao);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void agendarSessao(Sessao sessao) {
        var document = new Document();

        document.append("data", sessao.getData());
        document.append("idMestre", sessao.getMestreId());
        document.append("jogadoresIds", sessao.getJogadoresIds());
        document.append("periodo", sessao.getPeriodo().descricao);

        sessoesCollection.insertOne(document);
    }

    @Override
    public List<Sessao> buscarSessoes(Long idMestre, LocalDate dataInicio, LocalDate dataFim) {
        var resultado = new LinkedList<Document>();

        sessoesCollection
                .find(Filters.and(
                        Filters.eq("idMestre", idMestre),
                        Filters.gte("data", dataInicio),
                        Filters.lte("data", dataFim)
                ))
                .into(resultado);

        return resultado
                .stream()
                .map(this::documento2Sessao)
                .toList();
    }

    private Sessao documento2Sessao(Document documento) {
        return new Sessao(
                LocalDate.ofInstant(documento.get("data", Date.class).toInstant(), ZoneId.systemDefault()),
                documento.get("idMestre", Long.class),
                documento.getList("jogadoresIds", Long.class),
                Periodo.get(documento.getString("periodo"))
        );
    }
}
