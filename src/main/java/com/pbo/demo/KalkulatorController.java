package com.pbo.demo;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class KalkulatorController {
    private final OperasiRepository repository;

    public KalkulatorController(OperasiRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String halaman(@RequestParam(required = false) Long edit, Model model) {
        if (!model.containsAttribute("operator")) {
            if (edit == null) {
                isiForm(model, null, "", "+", "");
            } else {
                Operasi operasi = cari(edit);
                isiForm(model, edit, operasi.getBilangan1(), operasi.getOperator(), operasi.getBilangan2());
                model.addAttribute("hasil", operasi.getHasil());
            }
        }
        return tampilkan(model);
    }

    @PostMapping("/hitung")
    public String hitung(@RequestParam(required = false) Long id,
                         @RequestParam String bilangan1, @RequestParam String operator,
                         @RequestParam String bilangan2, Model model, RedirectAttributes redirect) {
        Operasi operasi = id == null ? new Operasi() : cari(id);
        try {
            BigDecimal angka1 = validasiAngka(bilangan1);
            BigDecimal angka2 = validasiAngka(bilangan2);
            if (!List.of("+", "-", "*", "/").contains(operator)) {
                throw new IllegalArgumentException("Pilih operator +, -, *, atau /.");
            }
            if ("/".equals(operator) && angka2.signum() == 0) {
                throw new IllegalArgumentException("Bilangan kedua tidak boleh nol untuk pembagian.");
            }
            operasi.isi(angka1, operator, angka2);
        } catch (IllegalArgumentException e) {
            isiForm(model, id, bilangan1, operator, bilangan2);
            model.addAttribute("pesan", e.getMessage());
            return tampilkan(model);
        }
        repository.save(operasi);
        redirect.addFlashAttribute("bilangan1", bilangan1);
        redirect.addFlashAttribute("operator", operator);
        redirect.addFlashAttribute("bilangan2", bilangan2);
        redirect.addFlashAttribute("hasil", operasi.getHasil());
        return "redirect:/";
    }

    @PostMapping("/hapus/{id}")
    public String hapus(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/";
    }

    private BigDecimal validasiAngka(String input) {
        if (!input.matches("[+-]?(?:[0-9]{1,12}(?:[.,][0-9]{0,12})?|[.,][0-9]{1,12})")) {
            throw new IllegalArgumentException("Masukkan angka dengan maksimal 12 digit sebelum dan sesudah desimal.");
        }
        return new BigDecimal(input.replace(',', '.'));
    }

    private Operasi cari(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Operasi tidak ditemukan."));
    }

    private void isiForm(Model model, Long id, String bilangan1, String operator, String bilangan2) {
        model.addAttribute("id", id);
        model.addAttribute("bilangan1", bilangan1);
        model.addAttribute("operator", operator);
        model.addAttribute("bilangan2", bilangan2);
    }

    private String tampilkan(Model model) {
        model.addAttribute("riwayat", repository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("operatorList", List.of("+", "-", "*", "/"));
        return "Kalkulator";
    }
}
