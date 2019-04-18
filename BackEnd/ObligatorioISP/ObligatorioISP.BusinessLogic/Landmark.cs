using ObligatorioISP.BusinessLogic.Exceptions;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace ObligatorioISP.BusinessLogic
{
    public class Landmark
    {
        private string title;
        private double latitude;
        private double longitude;
        private string description;
        private List<string> imagesPaths;
        private List<string> audiosPaths;

        public string Description { get { return description; } private set { SetDescription(value); } }
        public string Title { get { return title; } private set { SetTitle(value); } }
        public List<string> Images { get { return imagesPaths; } private set { SetImages(value); } }
        public List<string> Audios { get { return audiosPaths; } private set { SetAudios(value); } }

        public Landmark(string aTitle, double lat, double lng, string aDescription, string aPath)
        {
            SetCommonAttributes(aTitle, lat, lng, aDescription);
            imagesPaths = new List<string>();
            AddImage(aPath);
            audiosPaths = new List<string>();
        }

        //Constructor with a list of images' paths, instead of a single path.
        public Landmark(string aTitle, string aDescription, List<string> paths, double lat, double lng)
        {
            SetCommonAttributes(aTitle, lat, lng, aDescription);
            Images = paths;
            Audios = new List<string>();

        }
        //Constructor with images and audios lists.
        public Landmark(string aTitle, double lat, double lng, string aDescription, List<string> imagesPaths, List<string> audiosPaths) {
            SetCommonAttributes(aTitle, lat, lng, aDescription);
            Images = imagesPaths;
            Audios = audiosPaths;
        }

        private void SetCommonAttributes(string aTitle, double lat, double lng, string aDescription) {
            Title = aTitle;
            latitude = lat;
            longitude = lng;
            Description = aDescription;
        }

        private void SetTitle(string value)
        {
            if (String.IsNullOrWhiteSpace(value)) {
                throw new InvalidLandmarkException("Name can't be empty");
            }
            title = value;
        }

        private void SetDescription(string value)
        {
            if (value == null) {
                throw new InvalidLandmarkException("Description can't be null");
            }
            description = value;
        }

        public void AddImage(string aPath)
        {
            if (!File.Exists(aPath)) {
                throw new InvalidLandmarkException("Image"+aPath+" doesn't exist");
            }
            imagesPaths.Add(aPath);
        }

        private void SetImages(List<string> pathList)
        {
            if (pathList == null) {
                throw new InvalidLandmarkException("Images list can't be null");
            }
            if (pathList.Any(p => !File.Exists(p))) {
                throw new InvalidLandmarkException("Image doesn't exist");
            }
            if (!pathList.Any()) {
                throw new InvalidLandmarkException("Images list can't be empty");
            }
            imagesPaths = pathList;
        }

        private void SetAudios(List<string> pathList)
        {
            if (pathList == null)
            {
                throw new InvalidLandmarkException("Audios list can't be null");
            }
            if (pathList.Any(p => !File.Exists(p)))
            {
                throw new InvalidLandmarkException("Audio doesn't exist");
            }
            audiosPaths = pathList;
        }

        public void AddAudio(string path) {
            if (!File.Exists(path)) {
                throw new InvalidLandmarkException("Audio doesn't exist");
            }
            audiosPaths.Add(path);
        }
    }
}
