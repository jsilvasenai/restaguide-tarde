package br.senai.sp.cfp138.restaguide.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.senai.sp.cfp138.restaguide.annotation.Publico;
import br.senai.sp.cfp138.restaguide.model.TipoRestaurante;
import br.senai.sp.cfp138.restaguide.repository.TipoRestauranteRepository;

@RequestMapping("/api/tipoRestaurante")
@RestController
public class TipoRestauranteRestController {
	@Autowired
	private TipoRestauranteRepository repository;
	
	@Publico
	@RequestMapping(value="", method = RequestMethod.GET)
	public Iterable<TipoRestaurante> getRestaurantes(){
		return repository.findAll();
	}


	
}
