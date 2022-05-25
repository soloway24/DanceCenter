let addSongTitleField = document.getElementById("addSongTitleField");
let addSongsDiv = document.getElementById("addSongsDiv");
let addSongResetBtn = document.getElementById("addSongResetBtn");
let loadFileInput = document.getElementById("loadFileInput");
let titleAddDiv = document.getElementById("titleAddDiv");
let artistAddDiv = document.getElementById("artistAddDiv");


let editPostDescriptionField = document.getElementById("editPostDescriptionField");
let addSongsSubmitBtn = document.getElementById("addSongsSubmitBtn");
let addSongsResetBtn = document.getElementById("addSongsResetBtn");


addSongsSubmitBtn.onclick = async function (e) {

    const nFiles = loadFileInput.files.length;
    if(nFiles < 1){
        alert("Choose at least 1 file");
        e.preventDefault();
        return;
    }

    let formData = new FormData();
    for(let i = 0; i < nFiles; i++){
        formData.append("files", loadFileInput.files[i]);
    }

    let getFileTagsResponse = await fetch("/songs/multipleFileInfo", {
        method: "POST",
        body: formData,
    });
    let tagList;
    await getFileTagsResponse.json().then(res => { tagList = res; });

    let titles = [];
    let artists = [];
    for(let i = 0; i < nFiles; i++){
        titles.push(tagList[i].TITLE);
        const songArtists = tagList[i].ARTIST.split('/');
        artists.push(songArtists);
    }
    formData.append("titles", JSON.stringify(titles));
    formData.append("artists", JSON.stringify(artists));

    let addSongsResponse = await fetch("/songs/multipleAdd", {
                method: "POST",
                body: formData
            });
    let addedIdsJson = await addSongsResponse.json();
    let addedIds = JSON.stringify(addedIdsJson);
    addedIds = addedIds.substring(1, addedIds.length - 1);

    resetLoadFileInput();

    if(window.location.href.substring(window.location.href.length - 7) === "addPost")
        window.location.href = "/addPost/" + addedIds;
    else
        window.location.href = window.location.href + ',' + addedIds;

}

addSongsResetBtn.onclick = function () {
    resetLoadFileInput();
    resetPostDescription();
}

function resetLoadFileInput() {loadFileInput.value = ""; }
function resetPostDescription() {editPostDescriptionField = ""; }


const editTitleInputPrefix = "editTitleInput";
const editTitleDivPrefix = "editTitleDiv";
const editArtistsDivPrefix = "editArtistsDiv";
const editArtistsInputDivPrefix = "editArtistsInputDiv";
const addArtistInputPrefix = "addArtistInput";
const addArtistButtonPrefix = "addArtistButton";
const artistListDivPrefix = "artistListDiv";

loadFileInput.onchange = async function () {
    // let formData = new FormData();
    // const nFiles = loadFileInput.files.length;
    // let single =  nFiles === 1;
    // if(single){
    //     formData.append("file", loadFileInput.files[0]);
    // }else{
    //     for(let i = 0; i < nFiles; i++)
    //         formData.append("files", loadFileInput.files[i]);
    // }
    // console.log(formData.get("files"));
    //
    // if(single){
    //     let tagResponse = await fetch("/songs/fileInfo", {
    //         method: "POST",
    //         body: formData,
    //     });
    //     let tags;
    //     await tagResponse.json().then(res => { tags = res; });
    //
    //     formData.append("title", tags.TITLE);
    //     const artists = tags.ARTIST.split('/');
    //     formData.append("artists", artists);
    //     let addResponse = await fetch("/songs/add", {
    //         method: "POST",
    //         body: formData
    //     });
    //     if (addResponse.redirected)
    //         window.location.href = addResponse.url;
    //     console.log(addResponse);
    //
    // } else {
    //     let response = await fetch("/songs/multipleFileInfo", {
    //         method: "POST",
    //         body: formData,
    //     });
    //     let tagList;
    //     await response.json().then(res => { tagList = res; });
    //
    //     console.log(tagList);

        // let addSingleSongWrapper = document.createElement('div');
        // addSingleSongWrapper.classList.add("card");
        //
        // let addSingleSongDiv = document.createElement('div');
        // addSingleSongDiv.classList.add("card-body");
        //
        // //title
        // let editTitleDiv = document.createElement('div');
        // editTitleDiv.classList.add("form-group");
        // editTitleDiv.id = editTitleDivPrefix;
        //
        // let editTitleInput = document.createElement('input');
        // editTitleInput.id = editTitleInputPrefix;
        // editTitleInput.setAttribute("type", "text");
        // editTitleInput.setAttribute("name", "title");
        // editTitleInput.classList.add("form-control");
        // editTitleInput.value = tags.TITLE;
        //
        // let editTitleLabel = document.createElement('label');
        // editTitleLabel.setAttribute("for", editTitleInput.id);
        // editTitleLabel.innerHTML = "Title:";
        //
        // editTitleDiv.appendChild(editTitleLabel);
        // editTitleDiv.appendChild(editTitleInput);
        //
        //
        // //authors
        // let editArtistsDiv = document.createElement('div');
        // editArtistsDiv.classList.add("form-group");
        // editArtistsDiv.id = editArtistsDivPrefix;
        //
        // let editArtistsInputDiv = document.createElement('div');
        // editArtistsInputDiv.classList.add("form-group");
        // editArtistsInputDiv.id = editArtistsInputDivPrefix;
        //
        // let addArtistInput = document.createElement('input');
        // addArtistInput.id = addArtistInputPrefix;
        // addArtistInput.setAttribute("type", "text");
        //
        // let addArtistLabel = document.createElement('label');
        // addArtistLabel.setAttribute("for", addArtistInput.id);
        // addArtistLabel.innerHTML = "Add artist  :";
        //
        // let addArtistButton = document.createElement('button');
        // addArtistButton.id = addArtistButtonPrefix;
        // addArtistButton.classList.add("btn", "btn-primary");
        // addArtistButton.setAttribute("type", "button");
        // addArtistButton.innerHTML = "Add";
        // addArtistButton.onclick = function () {
        //     addArtistFromInput(addArtistInput, artistListDiv);
        // }
        //
        // let artistListDiv = document.createElement('div');
        // artistListDiv.classList.add("form-group");
        // artistListDiv.id = artistListDivPrefix;
        //
        // editArtistsInputDiv.appendChild(addArtistLabel);
        // editArtistsInputDiv.appendChild(addArtistInput);
        // editArtistsInputDiv.appendChild(addArtistButton);
        //
        // editArtistsDiv.appendChild(editArtistsInputDiv);
        // editArtistsDiv.appendChild(artistListDiv);
        //
        // addSingleSongDiv.appendChild(editTitleDiv);
        // addSingleSongDiv.appendChild(editArtistsDiv);
        //
        // addSingleSongWrapper.appendChild(addSingleSongDiv);
        //
        // addSongsDiv.firstChild.nextSibling.after(addSingleSongWrapper);
        //
        // addArtistToList(tags.ARTIST, artistListDiv);

    // }



}

function createEditInfoDiv(tagList) {
    if(tagList.length === undefined) {

    }

}

function createSingle() {
    let addSingleSongWrapper = document.createElement('div');
    addSingleSongWrapper.classList.add("card");

    let addSingleSongDiv = document.createElement('div');
    addSingleSongDiv.classList.add("card-body");

    //title
    let editTitleDiv = document.createElement('div');
    editTitleDiv.classList.add("form-group");
    editTitleDiv.id = editTitleDivPrefix;

    let editTitleInput = document.createElement('input');
    editTitleInput.id = editTitleInputPrefix;
    editTitleInput.setAttribute("type", "text");
    editTitleInput.setAttribute("name", "title");
    editTitleInput.classList.add("form-control");
    editTitleInput.value = tags.TITLE;

    let editTitleLabel = document.createElement('label');
    editTitleLabel.setAttribute("for", editTitleInput.id);
    editTitleLabel.innerHTML = "Title:";

    editTitleDiv.appendChild(editTitleLabel);
    editTitleDiv.appendChild(editTitleInput);


    //authors
    let editArtistsDiv = document.createElement('div');
    editArtistsDiv.classList.add("form-group");
    editArtistsDiv.id = editArtistsDivPrefix;

    let editArtistsInputDiv = document.createElement('div');
    editArtistsInputDiv.classList.add("form-group");
    editArtistsInputDiv.id = editArtistsInputDivPrefix;

    let addArtistInput = document.createElement('input');
    addArtistInput.id = addArtistInputPrefix;
    addArtistInput.setAttribute("type", "text");

    let addArtistLabel = document.createElement('label');
    addArtistLabel.setAttribute("for", addArtistInput.id);
    addArtistLabel.innerHTML = "Add artist  :";

    let addArtistButton = document.createElement('button');
    addArtistButton.id = addArtistButtonPrefix;
    addArtistButton.classList.add("btn", "btn-primary");
    addArtistButton.setAttribute("type", "button");
    addArtistButton.innerHTML = "Add";
    addArtistButton.onclick = function () {
        addArtistFromInput(addArtistInput, artistListDiv);
    }

    let artistListDiv = document.createElement('div');
    artistListDiv.classList.add("form-group");
    artistListDiv.id = artistListDivPrefix;

    editArtistsInputDiv.appendChild(addArtistLabel);
    editArtistsInputDiv.appendChild(addArtistInput);
    editArtistsInputDiv.appendChild(addArtistButton);

    editArtistsDiv.appendChild(editArtistsInputDiv);
    editArtistsDiv.appendChild(artistListDiv);

    addSingleSongDiv.appendChild(editTitleDiv);
    addSingleSongDiv.appendChild(editArtistsDiv);

    addSingleSongWrapper.appendChild(addSingleSongDiv);

    addSongsDiv.firstChild.nextSibling.after(addSingleSongWrapper);

    addArtistToList(tags.ARTIST, artistListDiv);
}
// <div className="form-group" id="titleAddDiv" style="display: none;">
//     <label>Title:</label>
//     <input type="text" className="form-control" name="title" id="addSongTitleField">
// </div>
//


function setAddSongTitleField(title) {
    if (title === null || title === undefined) {
        console.error("Song title is not set.");
        return;
    }
    if (!/\S/.test(title)) {
        console.error("Song title is blank.");
        return;
    }
    addSongTitleField.value = title;
}


// addSongSubmitBtn.onclick = function (e) {
//     let formData = new FormData(addSongForm);
// //check new form-data values
//     for (let p of formData) {
//         let title = p[0];
//         let value = p[1];
//         console.log(title, value)
//     }
//     e.preventDefault();
// }

function addArtistFromInput(artistInput, artistList) {
    addArtistToList(artistInput.value, artistList);
    artistInput.value = "";
}


function addArtistToList(artist, artistList) {
    if (artist === null || artist === undefined) {
        console.error("Artist is not set.");
        return;
    }
    if (!/\S/.test(artist)) {
        console.error("Artist is blank.");
        return;
    }
    if (artistListContains(artistList, artist)){
        console.error("Duplicate artist.");
        return;
    }
    let br = document.createElement('br');

    let textField = document.createElement('input');
    textField.setAttribute("type", "text");
    textField.setAttribute("name", "artists");
    textField.value = artist;

    let deleteButton = document.createElement('button');
    deleteButton.classList.add("btn", "btn-outline-secondary");
    deleteButton.setAttribute("type", "button");
    deleteButton.onclick = function () {
        artistList.removeChild(textField);
        artistList.removeChild(deleteButton);
        artistList.removeChild(br);
        if(artistList.childElementCount === 0)
            artistList.style.display = "none";
    }
    deleteButton.value = "Delete";
    deleteButton.innerHTML = "Delete";

    artistList.appendChild(textField);
    artistList.appendChild(deleteButton);
    artistList.appendChild(br);
}

function artistListContains(artistList, artist) {
    for ( let li of artistList.getElementsByTagName("li")) {
        if (li.textContent.toLowerCase() === artist.toLowerCase())
            return true;
    }
    return false;
}