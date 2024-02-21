import {setAuthenticatedUser} from "../../utils/auths";
import Navigate from "../Router/Navigate";
import Navbar from "../Navbar/Navbar";

const LoginPage = () => {
  const main = document.querySelector('main .row');
  main.innerHTML = `
<div class="col-12">
  <div class="row d-flex justify-content-center align-items-center" style="height:80vh;">
    <div class="col-12 col-md-4">
    <!-- Pills navs -->
    <ul id="form-nav" class="nav nav-pills nav-justified mb-5" id="ex1" role="tablist">
    <li class="nav-item" role="presentation">
        <button class="nav-link active" id="tab-login" data-mdb-toggle="pill" href="#pills-login" role="tab"
    aria-controls="pills-login" aria-selected="true">Se connecter</button>
    </li>
    <li class="nav-item" role="presentation">
            <button class="nav-link" id="tab-register" data-mdb-toggle="pill" href="#pills-register" role="tab"
    aria-controls="pills-register" aria-selected="false">S'inscrire</button>
    </li>
    </ul>
    <!-- Pills navs -->
    
    <!-- Pills content -->
    <div class="tab-content">
    <div class="tab-pane fade show active" id="pills-login" role="tabpanel" aria-labelledby="tab-login">
    <form id="login-form">
    <h4 class="fw-semibold">Connectez-vous</h4>
    <small>Pour avoir accès à toutes les fonctionnalités du site et un historique de vos objets</small>
    <div class="form-outline my-2">
    <label class="form-label" for="login-email">E-mail</label>
    <input type="email" id="login-email" name="login-email" class="form-control" />
    </div>
    <div class="form-outline mb-2">
    <label class="form-label" for="login-password">Mot de passe</label>
    <input type="password" id="login-password" name="login-password" class="form-control"/>
    </div>
    <div class="form-check">
    <input class="form-check-input" type="checkbox" value="" id="login-remember" name="login-remember" checked />
    <label class="form-check-label" for="login-remember"> Se souvenir de moi</label>
    </div>
    <button type="submit" class="btn btn-primary btn-block mt-4" style="width:100%;">Se connecter</button>
    </form>
    </div>
    <div class="tab-pane fade" id="pills-register" role="tabpanel" aria-labelledby="tab-register">
    <form id="register-form">
      <div class="row">
           <div class="form-group col py-2">
            <label class="ff-rubik mb-1 ms-2" for="register-firstname">Prénom<b class="warning-color">*</b></label>
            <input type="text" class="form-control p-2 px-4" id="register-firstname" placeholder="John"> 
          </div>
           <div class="form-group col py-2">
            <label class="ff-rubik mb-1 ms-2" for="register-lastname">Nom<b class="warning-color">*</b></label>
            <input type="text" class="form-control p-2 px-4" id="register-lastname" placeholder="Doe">
          </div>
        </div>
        <div class="form-group col py-2">
          <label class="ff-rubik mb-1 ms-2" for="register-phone">Numéro de téléphone<b class="warning-color">*</b></label>
          <input type="tel" class="form-control p-2 px-4" id="register-phone" placeholder="+32412345678"> 
        </div>
        <div class="form-group py-2">
          <label class="ff-rubik mb-1 ms-2" for="register-email">E-mail<b class="warning-color">*</b></label>
          <input type="email" class="form-control p-2 px-4" id="register-email" placeholder="************"> 
        </div>
        <div class="form-group py-2">
          <label class="ff-rubik mb-1 ms-2" for="register-password">Mot de passe<b class="warning-color">*</b></label>
          <input type="password" class="form-control p-2 px-4" id="register-password" placeholder="************"> 
        </div>
        <div class="form-group py-2" >
          <label class="ff-rubik mb-1 ms-2" for="register-password-confirm">Confirmez votre mot de passe<b class="warning-color">*</b></label>
          <input type="password" class="form-control p-2 px-4" id="register-password-confirm" placeholder="************"> 
        </div>
        <div class="form-group py-2" >
          <label class="ff-rubik mb-1 ms-2" for="register-image">Photo ou avatar<b class="warning-color">*</b></label>
          <input type="file" accept="image/png, image/jpeg" class="form-control p-2 px-4" id="register-image" required> 
        </div>
    <!-- Submit button -->
    <button type="submit" class="btn btn-primary btn-block mt-3" style="width:100%;">Créer un compte</button>
    </form>
    </div>
    </div>
    <!-- Pills content -->
         <div class="alert-message"></div>
    </div>
  </div>
</div>
  `;
  const loginForm = document.getElementById("login-form");
  loginForm.addEventListener('submit', onLogin);
  const registerForm = document.getElementById("register-form");
  registerForm.addEventListener('submit', onRegister);
  const navPills = document.querySelectorAll('#form-nav li button');
  navPills.forEach((elem) => {
    elem.addEventListener('click', () => {
      if(elem.getAttribute('id') === "tab-login"){
        document.getElementById("tab-login").classList.add("active");
        document.getElementById("tab-register").classList.remove("active");
        document.getElementById("pills-login").classList.add("active");
        document.getElementById("pills-login").classList.add("show");
        document.getElementById("pills-register").classList.remove("active");
        document.getElementById("pills-register").classList.remove("show");
      }else if(elem.getAttribute("id") === "tab-register"){
        document.getElementById("tab-register").classList.add("active");
        document.getElementById("tab-login").classList.remove("active");
        document.getElementById("pills-login").classList.remove("active");
        document.getElementById("pills-login").classList.remove("show");
        document.getElementById("pills-register").classList.add("active");
        document.getElementById("pills-register").classList.add("show");
      }
    })
  })
};

async function onLogin(e) {
  e.preventDefault();
  const email = document.querySelector('#login-email').value;
  const password = document.querySelector('#login-password').value;
  const rememberMe = document.querySelector("#login-remember").checked;
  const textAlertLogin = document.querySelector(`div.alert-message`);

  const options = {
    method: 'POST',
    body: JSON.stringify({
      email,
      password,
    }),
    headers: {
      'Content-Type': 'application/json'
    },
  };

  const response = await fetch('http://localhost:8080/users/login', options);
  if (!response.ok) {
    textAlertLogin.innerHTML = "<p>Vos identifiants sont incorrects. Veuillez réessayer.</p>";
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`)
  }

  const authenticatedUser = await response.json();
  setAuthenticatedUser(authenticatedUser, rememberMe);

  Navbar();
  Navigate('/profil');
}

async function onRegister(e) {
  e.preventDefault();
  const firstname = document.querySelector('#register-firstname').value;
  const lastname = document.querySelector('#register-lastname').value;
  const phone = document.querySelector('#register-phone').value;
  const email = document.querySelector('#register-email').value;
  const password = document.querySelector('#register-password').value;
  const passwordConfirm = document.querySelector('#register-password-confirm').value;
  const image = document.querySelector('#register-image');

  if(password !== passwordConfirm){
    const errorDiv = document.getElementById("register-error");
    errorDiv.innerHTML = "Les mots de passe ne correspondent pas.";
  }else{
    const userEntier = new FormData();
    userEntier.append('email', email);
    userEntier.append('password', password);
    userEntier.append('firstname', firstname);
    userEntier.append('lastname', lastname);
    userEntier.append('phone', phone);
    userEntier.append('file', image.files[0]);

    const options = {
      method: 'POST',
      body: userEntier
    };

    const response = await fetch('http://localhost:8080/users/register', options);
    if (!response.ok) {
      const textAlertLogin = document.querySelector(`div.alert-message`);
      textAlertLogin.innerHTML = "<p>Une erreur est survenue. Veuillez réessayer.</p>";
      throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
    }
    const textAlertLogin = document.querySelector(`div.alert-message`);
    textAlertLogin.innerHTML = "<p>Merci de votre inscription, pensez à vous connecter !</p>";
    Navbar();
    const tabLogin = document.querySelector(`button#tab-login`);
    tabLogin.click();
    // Navigate('/connexion');
  }
}

export default LoginPage;
