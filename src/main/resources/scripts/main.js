let artistInputField = document.getElementById("artistInputField");
let artistList = document.getElementById("artistList");
let addSongForm = document.getElementById("addSongForm");
let addSongSubmitBtn = document.getElementById("addSongSubmitBtn");
let artistsSet = document.getElementById("artistsSet");

// addSongSubmitBtn.onclick = function (e) {
//     e.preventDefault();
//     let lis = Array.from(artistList.getElementsByTagName("li"));
//     let artists = [];
//     for (let el of lis)
//     {
//         artists.push(el.innerText);
//     }
//     artistsSet.value = artists;
//     let formData = new FormData(addSongForm);
//
//     for (let p of formData) {
//         let name = p[0];
//         let value = p[1];
//         if (name === "artists")
//         {
//             let arr = [];
//             arr = value.split(',');
//             formData.set("artists", JSON.stringify(arr));
//         }
//     }
//
// //check new form-data values
//     for (let p of formData) {
//         let name = p[0];
//         let value = p[1];
//         console.log(name, value)
//     }
//
//     fetch("/songs/add", {
//         method: "POST",
//         body: formData
//     }).then(res => {
//         console.log("Request complete! response:", res);
//         res.json().then(function(data) {
//             console.log(data);
//         });
//     });
// }


function addArtistToList() {
    if (artistInputField.value === "" || artistInputField.value === undefined)
        return;
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