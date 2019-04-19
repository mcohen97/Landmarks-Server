using ObligatorioISP.BusinessLogic.Exceptions;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace ObligatorioISP.BusinessLogic
{
    public class Landmark
    {
        private int id;
        private string title;
        private double latitude;
        private double longitude;
        private string description;
        private List<string> imagesPaths;
        private List<string> audiosPaths;

        public int Id { get { return id; }private set { SetId(value); } }
        public string Title { get { return title; } private set { SetTitle(value); } }
        public string Description { get { return description; } private set { SetDescription(value); } }
        public List<string> Images { get { return imagesPaths; } private set { SetImages(value); } }
        public List<string> Audios { get { return audiosPaths; } private set { SetAudios(value); } }

        public Landmark(string aTitle, double lat, double lng, string aDescription, string aPath)
        {
            SetCommonAttributes(0, aTitle, lat, lng, aDescription);
            imagesPaths = new List<string>();
            AddImage(aPath);
            audiosPaths = new List<string>();
        }

        public Landmark(int anId, string aTitle, double lat, double lng, string aDescription, string aPath):this(aTitle,lat,lng,aDescription,aPath)
        {
            Id = anId;
        }

        //Constructor with a list of images' paths, instead of a single path.
        public Landmark(int anId, string aTitle, string aDescription, List<string> paths, double lat, double lng)
        {
            SetCommonAttributes(anId,aTitle, lat, lng, aDescription);
            Images = paths;
            Audios = new List<string>();

        }
        //Constructor with images and audios lists.
        public Landmark(int anId, string aTitle, double lat, double lng, string aDescription, List<string> imagesPaths, List<string> audiosPaths) {
            SetCommonAttributes(id,aTitle, lat, lng, aDescription);
            Images = imagesPaths;
            Audios = audiosPaths;
        }

        private void SetCommonAttributes(int anId, string aTitle, double lat, double lng, string aDescription) {
            Id = anId;
            Title = aTitle;
            latitude = lat;
            longitude = lng;
            Description = aDescription;

        }

        private void SetId(int value)
        {
            if (value<0)
            {
                throw new InvalidLandmarkException("id can't be negative");
            }
            id = value;
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
