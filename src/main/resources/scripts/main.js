let artistInputField = document.getElementById("artistInputField");
let artistList = document.getElementById("artistList");
let addSongForm = document.getElementById("addSongForm");
let getFileInfoForm = document.getElementById("getFileInfoForm");
let addSongSubmitBtn = document.getElementById("addSongSubmitBtn");
let addSongResetBtn = document.getElementById("addSongResetBtn");
let artistsSet = document.getElementById("artistsSet");
let loadFileInput = document.getElementById("loadFileInput");

loadFileInput.onchange = function () {
    let formData = new FormData();
    formData.append("file", loadFileInput.files[0]);
    console.log(formData.get("file"));

    fetch("/songs/fileInfo",{
        // headers: {'Content-Type': false },
        method: "POST",
        body: formData,
    }).then(res => {
        console.log("Request complete! response:", res);
        res.json().then(function(result) {
            console.log(result);
        });
    });
}

addSongResetBtn.onclick = function () {
    artistList.innerHTML = "";
}

// addSongSubmitBtn.onclick = function (e) {
//     let formData = new FormData(addSongForm);
// //check new form-data values
//     for (let p of formData) {
//         let name = p[0];
//         let value = p[1];
//         console.log(name, value)
//     }
//     e.preventDefault();
// }


function addArtistToList() {
    if (artistInputField.value === "" || artistInputField.value === undefined)
        return;
    if (artistListContains(artistInputField.value)){
        alert("Duplicate artist.");
        return;
    }
    let artist = artistInputField.value;
    let input = document.createElement('input');
    input.setAttribute("type", "hidden");
    input.setAttribute("name", "artists");
    input.value = artist;
    let entry = document.createElement('li');
    entry.classList.add('list-group-item');
    entry.appendChild(document.createTextNode(artist));
    artistList.appendChild(entry);
    artistList.appendChild(input);
    artistInputField.value = "";
}

function artistListContains(artist) {
    for ( let li of artistList.getElementsByTagName("li"))
    {
        if (li.textContent.toLowerCase() === artist.toLowerCase())
            return true;
    }
    return false;
}