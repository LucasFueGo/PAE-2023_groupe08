// eslint-disable-next-line import/named
import gif from "../../img/loader2.gif";
import ObjectPage from "./ObjectPage";

const HomePage = () => {
  const main = document.querySelector('main .row');
  main.innerHTML = `
      <div class="px-3 py-5 text-center">
    <h1 class="display-6 ff-rubik fw-normal">Bievenue à la RessourceRie</h1>
    <div class="col-lg-8 mx-auto">
      <p class="lead mb-4 ff-rubik">La RessourceRie a été créée par M. Riez pour compenser le gaspillage de beaux objets dans les parcs à conteneurs! Notre objectif? Revendre des articles à des prix démocratiques. Les objets sont nettoyés et/ou restaurés selon les besoins.</p>
    </div>
  </div>
      <div class="container mt-4 px-5">
        <div class="row">
        <div class="col-md-12 content-filter row justify-content-end mb-4">
            <div class="col-6 col-md-3">
              <select class="form-select select-type-object">
                <option selected value="0">Tous les types</option>
              </select>
            </div>
        </div>
        <div class="col-md-12 content-all-object object-available row">
            <div class="loader-div" style="text-align: center">
              <img src="${gif}" alt="loader">
            </div>
        </div>
       </div>
       </div>`;

  getAllObjects().then((objects) => {
    const contentAllObject = document.querySelector('.content-all-object');
    contentAllObject.innerHTML = displayObjects(objects);

    const objectsLinks = document.querySelectorAll('.link');

    objectsLinks.forEach((linkObj) => {
      const objectLinkId = linkObj.dataset?.id;
      linkObj.setAttribute("data-uri",  `/objets?IdObject=${objectLinkId}`);
      linkObj.addEventListener('click', (elem) => {
        const uri = linkObj.dataset?.uri;
        elem.preventDefault();
        const path = uri.split("?")[0];
        if (path) {
          const urlParams = new URLSearchParams(uri.split("?")[1]);
          const idObject = urlParams.get("IdObject");
          window.history.pushState({}, '', `/objets?IdObject=${idObject}`);
          ObjectPage(idObject);
        }
      })
    });
    getAllType();
  });
};

async function getAllType() {
  const response = await fetch('http://localhost:8080/filter/getAll');
  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);
  const filters = await response.json();

  const contentFilterType = document.querySelector('.select-type-object');

  filters.forEach((filter) => {
    contentFilterType.innerHTML += `
      <option value="${filter.id}">${filter.name}</option>
    `;
  });

  const typeSelect = document.querySelector('.select-type-object');
  typeSelect.addEventListener('change', () => {

    const objects = document.querySelectorAll('div.single-object');

    /* eslint-disable no-param-reassign */
    objects.forEach((obj) => {
      if (obj.dataset.idtype === typeSelect.value) {
        obj.style.display = "block";
      } else if (Number(typeSelect.value) === 0) {
        obj.style.display = "block";
      }else{
        obj.style.display = "none";
      }
    })
  })
}

async function getAllObjects() {
  const response = await fetch('http://localhost:8080/object/getAllHome');
  if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
  return response.json();
}

function displayObjects(objects) {
  let allInfo = "";

  if(objects.length === 0) {
    allInfo = `<div class="info_top"><h1 class="text-center mt-5 mb-5">Des objets seront bientôt disponibles!</h1></div>`;
    return allInfo;
  }

  objects?.forEach((object) => {
    let priceFinal = object.sellingPrice;
    let unite;
    if(priceFinal === 0) {
      priceFinal = "A venir";
      unite = "";
    } else {
      priceFinal = priceFinal.toFixed(2);
      unite = "€";
    }
    allInfo += `<div class="col-12 link col-lg-3 py-2 single-object" data-id="${object.id}" data-idType="${object.idType}" data-uri="">
      <div class="card rounded-0 border-2">
        <img class="image-object card-img-top rounded-0" data-id="${object.id}" src="${object.url == null ? 'https://plchldr.co/i/500x350?bg=111111' : object.url}" >
          <div class="card-body">
            <h5 class="card-title fw-semibold fs-5">${object.description}</h5>
            <h6 class="card-subtitle mb-2 fw-bold"><i class="fa-solid fa-tag"></i> ${priceFinal} ${unite}</h6>
            <p class="card-text rounded-5 border border-1 border-dark px-3 py-1 ${object.nameType.toLowerCase()}">${object.nameType}</p>
          </div>
          <span class="info_object_disponibility card-status p-2 ${object.state}">${object.stateComplete}</span>
      </div></div>`;
  })
  return allInfo;
}

export default HomePage;
