package br.senai.sp.cfp138.restaguide.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.senai.sp.cfp138.restaguide.model.Restaurante;
import br.senai.sp.cfp138.restaguide.repository.RestauranteRepository;
import br.senai.sp.cfp138.restaguide.repository.TipoRestauranteRepository;
import br.senai.sp.cfp138.restaguide.util.FirebaseUtil;

@Controller
public class RestauranteController {
	@Autowired
	private TipoRestauranteRepository repTipo;
	@Autowired
	private RestauranteRepository repRest;
	@Autowired
	private FirebaseUtil firebaseUtil;

	@RequestMapping("formRestaurante")
	public String form(Model model) {
		model.addAttribute("tipos", repTipo.findAll());
		return "restaurante/form";
	}

	@RequestMapping("salvarRestaurante")
	public String salvarRestaurante(Restaurante restaurante, @RequestParam("fileFotos") MultipartFile[] fileFotos) {
		// String para a url das fotos
		String fotos = restaurante.getFotos();
		// percorrer cada arquivo que foi submetido no formulário
		for (MultipartFile arquivo : fileFotos) {
			// verificar se o arquivo está vazio
			if (arquivo.getOriginalFilename().isEmpty()) {
				// vai para o próximo arquivo
				continue;
			}
			// faz o upload para a nuvem e obtém a url gerada
			try {
				fotos += firebaseUtil.uploadFile(arquivo) + ";";
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		// atribui a String fotos ao objeto restaurante
		restaurante.setFotos(fotos);
		repRest.save(restaurante);
		return "redirect:formRestaurante";
	}

	@RequestMapping("/listarRestaurante/{page}")
	public String listar(Model model, @PathVariable("page") int page) {
		// caso queira ordenar por algum campo, acrescenta-se o Sort.by no 3º parâmetro
		PageRequest pageable = PageRequest.of(page - 1, 6, Sort.by(Sort.Direction.ASC, "nome"));
		Page<Restaurante> restaPage = repRest.findAll(pageable);
		int totalPages = restaPage.getTotalPages();
		model.addAttribute("rests", restaPage.getContent());
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", page);
		List<Integer> pageNumbers = new ArrayList<Integer>();
		for (int i = 0; i < totalPages; i++) {
			pageNumbers.add(i + 1);
		}
		model.addAttribute("pageNumbers", pageNumbers);
		return "restaurante/lista";
	}

	@RequestMapping("/alterarRest")
	public String alterarRestaurante(Model model, Long idRest) {
		Restaurante restaurante = repRest.findById(idRest).get();
		model.addAttribute("restaurante", restaurante);
		return "forward:/formRestaurante";
	}

	@RequestMapping("/excluirRestaurante")
	public String excluirRestaurante(Long idRestaurante) {
		Restaurante rest = repRest.findById(idRestaurante).get();
		if (rest.getFotos().length() > 0) {
			for (String foto : rest.verFotos()) {
				firebaseUtil.deletar(foto);
			}
		}
		repRest.delete(rest);
		return "redirect:/listarRestaurante/1";
	}

	@RequestMapping("/excluirFotoRestaurante")
	public String excluirFoto(Long idRestaurante, int numFoto, Model model) {
		// busca o restaurante no banco de dados
		Restaurante rest = repRest.findById(idRestaurante).get();
		// pegar a String da foto a ser excluída
		String fotoUrl = rest.verFotos()[numFoto];
		// excluir do firebase
		firebaseUtil.deletar(fotoUrl);
		// "arranca" a foto da String fotos
		rest.setFotos(rest.getFotos().replace(fotoUrl + ";", ""));
		// salva no BD o objeto rest
		repRest.save(rest);
		// adiciona o rest na Model
		model.addAttribute("restaurante", rest);
		// encaminhar para o form
		return "forward:/formRestaurante";
	}
}
