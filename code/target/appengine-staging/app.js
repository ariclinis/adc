function RegisterUser(){
    var userid = document.getElementById("userid").value;
    var nameuser = document.getElementById("username").value;
    var pwd = document.getElementById("pwd").value;
    var pwd_confirmation = document.getElementById("pwd_confirmation").value;
    var email = document.getElementById("email").value;
    
    var profile = document.getElementById("profile").value;
    var nif = document.getElementById("nif").value;
    var phone = document.getElementById("phone").value;
    var fix_number = document.getElementById("fix_number").value;
    var ocupation = document.getElementById("ocupation").value;
    var work_place = document.getElementById("work_place").value;
    //Address
    var street = document.getElementById("street").value;
    var postal_code = document.getElementById("postal_code").value;
    var location = document.getElementById("location").value;

    var data = JSON.stringify({
        "userid": userid,
        "name": nameuser,
        "password": pwd,
        "pwd_confirmation": pwd_confirmation,
        "email": email,
        "profile": profile,
        "nif": nif,
        "fix_number": fix_number,
        "phone": phone,
        "ocupation": ocupation,
        "work_place": work_place,
        "street": street,
        "location": location,
        "postal_code": postal_code
      });

    var xhr = new XMLHttpRequest();

    xhr.addEventListener("readystatechange", function() {
        if(this.readyState === 4) {
            if (xhr.status == 200) {
                alert("Utilizador Registado")
            } else if (xhr.status == 500) {
                alert(this.responseText);
            } else if (xhr.status == 400){
                alert("Missing or wrong parameter.");
            } else {
                alert(this.responseText);
            }
        }
    });

    xhr.open("POST", "https://avalicao-individual-adc.oa.r.appspot.com/rest/user/register",true);
    xhr.setRequestHeader("Content-Type", "application/json");
    
    xhr.send(data);
    
}

function LoginUser(){

    var userid = document.getElementById("userid").value;
    var pwd = document.getElementById("pwd").value;

    var data = JSON.stringify({
    "userid": userid,
    "password": pwd
    });

    var xhr = new XMLHttpRequest();
    xhr.withCredentials = true;

    xhr.addEventListener("readystatechange", function() {
    if(this.readyState === 4) {
        if (xhr.status == 200) {
            const u = this.responseText;
            var xhrm = new XMLHttpRequest();
            xhrm.open("GET", "ttps://avalicao-individual-adc.oa.r.appspot.com/rest/user/"+userid,true);
            xhrm.setRequestHeader("Content-Type", "application/json");
            xhrm.send()
            const user_get= this.responseText;
            
            const userData = JSON.stringify({
                "username": u.username, 
                "tokenID": u.tokenID, 
                "creationData": u.creationData, 
                "experationData": u.experationData,
                "userid": userid,
                "username": user_get.name,
                "role": user_get.role,
                "status": user_get.status
            });
            window.sessionStorage.setItem('user',data);
            window.location.replace("/painel.html");
        } else if (xhr.status == 500) {
            alert(this.responseText);
        } else if (xhr.status == 400){
            alert("Missing or wrong parameter.");
        } else {
            alert(this.responseText);
        }
    }
    
  });
  
  xhr.open("POST", "https://avalicao-individual-adc.oa.r.appspot.com/rest/login");
  xhr.setRequestHeader("Content-Type", "application/json");
  
  xhr.send(data);
}