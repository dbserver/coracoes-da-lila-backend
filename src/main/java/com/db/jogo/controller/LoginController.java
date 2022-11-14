package com.db.jogo.controller;

import com.db.jogo.model.Admin;
import com.db.jogo.model.Autenticacao;
import com.db.jogo.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Autenticacao> verificaSenha(@RequestBody Admin admin, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<Autenticacao>(new Autenticacao(false), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Autenticacao>(new Autenticacao(loginService.verificaSenha(admin.getSenha())), HttpStatus.OK);
    }
}
