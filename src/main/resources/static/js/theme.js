function toggleTheme(){

    document.body.classList.toggle("dark");

    if(document.body.classList.contains("dark")){

        localStorage.setItem("theme","dark");

        document.getElementById("themeBtn").innerHTML="☀️";

    }

    else{

        localStorage.setItem("theme","light");

        document.getElementById("themeBtn").innerHTML="🌙";

    }

}

document.addEventListener("DOMContentLoaded", function () {

    let theme = localStorage.getItem("theme");

    let btn = document.getElementById("themeBtn");

    if(theme === "dark"){

        document.body.classList.add("dark");

        if(btn){
            btn.innerHTML = "☀️";
        }

    }else{

        if(btn){
            btn.innerHTML = "🌙";
        }

    }

});