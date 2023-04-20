document.addEventListener("DOMContentLoaded", () => {
    var user = window.sessionStorage.getItem('user');
    const box = document.getElementById('nameuser');
    const text = document.createTextNode(user.userid);
    var c = JSON.stringify(text);
    box.appendChild(c);
});

function UpdateUser(){
    var userid = document.getElementById("userid").value;
    var nameuser = document.getElementById("username").value;
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
        "name": nameuser,
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
            if(this.readyState === 4) {
                if (xhr.status == 200) {
                    alert("Utilizador Atualizado")
                } else if (xhr.status == 500) {
                    alert(this.responseText);
                } else if (xhr.status == 400){
                    alert("Missing or wrong parameter.");
                } else {
                    alert(this.responseText);
                }
            }
        }
    });

    xhr.open("PUT", "https://avalicao-individual-adc.oa.r.appspot.com/rest/user/register",true);
    xhr.setRequestHeader("Content-Type", "application/json");
    
    xhr.send(data);
    
}

function RemoveUser(){
    var userid = document.getElementById("userid").value;
    var user_id_to_use = window.sessionStorage.getItem('user').userid;
    var xhr = new XMLHttpRequest();
    data={user_id_to_use:userid, user_id_session:user_id_to_use, role:"", token:1}
    xhr.addEventListener("readystatechange", function() {
        if(this.readyState === 4) {
            if(this.readyState === 4) {
                if (xhr.status == 200) {
                    alert("Utilizador Removido")
                } else if (xhr.status == 500) {
                    alert(this.responseText);
                } else if (xhr.status == 400){
                    alert("Missing or wrong parameter.");
                } else {
                    alert(this.responseText);
                }
            }
        }
    });

    xhr.open("DELETE", "https://avalicao-individual-adc.oa.r.appspot.com/rest/user/delete",true);
    xhr.setRequestHeader("Content-Type", "application/json");
    
    xhr.send(data);

}