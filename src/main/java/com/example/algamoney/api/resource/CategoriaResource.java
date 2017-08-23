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
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

//Satus code:
//2xx --> Sucesso
//4xx --> Erro Cliente
//5xx --> Erro no sevico/servidor
//Properties jackson transforma/enxerga json para java

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

	@Autowired
	private CategoriaRepository categoriaRepository;
	
	//criacao da lista de categoria
	@GetMapping
	public List<Categoria> listar() {
		return categoriaRepository.findAll();
	}
	
	@Autowired
	private ApplicationEventPublisher publicher;
	
	@PostMapping
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) { 
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
		publicher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		
		//retornar a categoriaSalva para mostrar o codigo ou qualquer outro campo
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	//busca a categoria pela uri passando o mapping e o codigo
	@GetMapping("/{codigo}")
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		Categoria categoria = categoriaRepository.findOne(codigo);
		return categoria != null ? ResponseEntity.ok(categoria) : ResponseEntity.notFound().build();  
	}
	
}