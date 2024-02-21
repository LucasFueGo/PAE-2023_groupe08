const getAuthenticatedUser = () => {
 const user = localStorage.getItem('user') ? localStorage.getItem('user') : sessionStorage.getItem('user');
 return JSON.parse(user);
}

const setAuthenticatedUser = (authenticatedUser, rememberMe) => {
  if(rememberMe) {
    localStorage.setItem('token', JSON.stringify(authenticatedUser.token));
    localStorage.setItem('user', JSON.stringify(authenticatedUser.user))
  }else{
    sessionStorage.setItem('token',JSON.stringify(authenticatedUser.token));
    sessionStorage.setItem('user',JSON.stringify(authenticatedUser.user));
  }
};

const updateAuthenticatedUser = (authenticatedUser) => {
  if(localStorage.getItem('user') !== null){
    localStorage.setItem('user',JSON.stringify(authenticatedUser.user));
  }else{
    sessionStorage.setItem('user',JSON.stringify(authenticatedUser.user));

  }
}

const clearAuthenticatedUser = () => {
  localStorage.removeItem('user');
  localStorage.removeItem('token');
  sessionStorage.removeItem('user');
  sessionStorage.removeItem('token');
};

const getToken = () => {
  let tokenUser = JSON.parse(localStorage.getItem('token'));

  if(tokenUser == null){
    tokenUser = JSON.parse(sessionStorage.getItem('token'));
  }
  return tokenUser;
};


const refreshAndCheckToken = async () => {
  let session = false;
  let tokenUser = JSON.parse(localStorage.getItem('token'));

  if(tokenUser == null){
    tokenUser = JSON.parse(sessionStorage.getItem('token'));
    session = true;
  }

  if(tokenUser != null){
    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
    };

    const response = await fetch(`http://localhost:8080/users/refresh?token=${tokenUser}`, options);
    if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);
    const newUser = await response.json();

    if(session){
      sessionStorage.setItem('user',JSON.stringify(newUser));
    } else {
      localStorage.setItem('user', JSON.stringify(newUser));
    }
  }

}

const isAuthenticated = () => sessionStorage.getItem('user') != null || localStorage.getItem('user') != null;

// eslint-disable-next-line object-curly-newline
export { updateAuthenticatedUser, getAuthenticatedUser, setAuthenticatedUser, clearAuthenticatedUser, isAuthenticated, refreshAndCheckToken, getToken};