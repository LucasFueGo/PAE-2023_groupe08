import {refreshAndCheckToken} from "./auths";

const clearPage = () => {
  refreshAndCheckToken().then(() => {
    const main = document.querySelector('main');
    main.innerHTML = '';
  })
};

export default clearPage ;
