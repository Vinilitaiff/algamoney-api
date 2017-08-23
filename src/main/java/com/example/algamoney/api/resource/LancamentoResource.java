package com.example.algamoney.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.LancamentoRepository;
 
//Satus code:
//2xx --> Sucesso
//4xx --> Erro Cliente
//5xx --> Erro no sevico/servidor
//Properties jackson transforma/enxerga json para java

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	// criacao da lista de Lancamento
	@GetMapping
	public List<Lancamento> listar() {
		return lancamentoRepository.findAll();
	}
	
	@Autowired
	private ApplicationEventPublisher publicher;
	

	@PostMapping
	public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) { 
		Lancamento lancamentoSalva = lancamentoRepository.save(lancamento);
		
		publicher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalva.getCodigo()));
		
		//retornar a lancamentoSalva para mostrar o codigo ou qualquer outro campo
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalva);
	}
	
	// busca a pessoa pela uri passando o mapping e o codigo
	@GetMapping("/{codigo}")
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
		Lancamento lancamento = lancamentoRepository.findOne(codigo);
		return lancamento != null ? ResponseEntity.ok(lancamento) : ResponseEntity.notFound().build();
	}
	

}