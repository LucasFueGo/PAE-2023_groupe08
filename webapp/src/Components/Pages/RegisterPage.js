import Navbar from "../Navbar/Navbar";
import Navigate from "../Router/Navigate";

const RegisterPage = () => {
  const main = document.querySelector('main .row');
  main.innerHTML = `
  <div id="register-page" class="row justify-content-center">
    <div id="register-wrapper" class="col-10 col-sm-7 col-lg-8 p-5">
      <form id="register-form" class="step-1 row justify-content-center">
          <div class="col-12">
          <div class="row justify-content-center">
            <div class="col-12">
              <h3 id="register-title" class="ff-rubik">Création de compte</h3>
              <p>Pour créer un compte, veuillez remplir les informations ci-dessous. Nous avons besoin de quelques détails pour vous offrir la meilleure expérience possible. Vous pourrez toujours les modifier sur votre profil par la suite.</p>
            </div>
          </div>
        </div>
      <div class="col-10">
      <div class="row">
           <div class="form-group col py-2">
            <label class="ff-rubik mb-1 ms-2" for="register-firstname">Prénom<b class="warning-color">*</b></label>
            <input type="text" class="form-control p-2 px-4 rounded-0 shadow-none" id="register-firstname" placeholder="John"> 
          </div>
           <div class="form-group col py-2">
            <label class="ff-rubik mb-1 ms-2" for="register-lastname">Nom<b class="warning-color">*</b></label>
            <input type="text" class="form-control p-2 px-4 rounded-0 shadow-none" id="register-lastname" placeholder="Doe">
          </div>
        </div>
        <div class="form-group col py-2">
          <label class="ff-rubik mb-1 ms-2" for="register-phone">Numéro de téléphone<b class="warning-color">*</b></label>
          <input type="tel" class="form-control p-2 px-4 rounded-0 shadow-none" id="register-phone" placeholder="+32412345678"> 
        </div>
        <div class="form-group py-2">
          <label class="ff-rubik mb-1 ms-2" for="register-email">E-mail<b class="warning-color">*</b></label>
          <input type="email" class="form-control p-2 px-4 rounded-0 shadow-none" id="register-email" placeholder="************"> 
        </div>
        <div class="form-group py-2">
          <label class="ff-rubik mb-1 ms-2" for="register-password">Mot de passe<b class="warning-color">*</b></label>
          <input type="password" class="form-control p-2 px-4 rounded-0 shadow-none" id="register-password" placeholder="************"> 
        </div>
        <div class="form-group py-2" >
          <label class="ff-rubik mb-1 ms-2" for="register-password-confirm">Confirmez votre mot de passe<b class="warning-color">*</b></label>
          <input type="password" class="form-control p-2 px-4 rounded-0 shadow-none" id="register-password-confirm" placeholder="************"> 
        </div>
        <div class="form-group py-2" >
          <label class="ff-rubik mb-1 ms-2" for="register-image">Photo ou avatar<b class="warning-color">*</b></label>
          <input type="file" accept="image/png, image/jpeg" class="form-control p-2 px-4 rounded-0 shadow-none" id="register-image" required> 
        </div>
        <p class="pb-1" id="register-error"></p>
        <p>Vous avez déjà un compte? <a class="accent-color" href="/login" data-uri="/login">Connectez-vous</a></p>
        <div class="row justify-content-around align-items-center">
          <button id="register-submit" type="submit" class="btn row rounded-0 mt-2 py-2 px-3 background-accent-color">S'inscrire</button>
        </div>
        </div>
      </form>
     </div>
  </div>`;
  const form = document.getElementById("register-form");
  form.addEventListener('submit', onRegister);
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
    errorDiv.innerHTML = "Passwords do not match.";
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
    if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

    Navbar();
    Navigate('/connexion');
  }
}

export default RegisterPage;
