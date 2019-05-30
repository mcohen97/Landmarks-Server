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
        public double Latitude { get; private set; }
        public double Longitude { get; private set; }
        private string description;
        private ICollection<string> imagesPaths;
        private ICollection<string> audiosPaths;

        public int Id { get { return id; }private set { SetId(value); } }
        public string Title { get { return title; } private set { SetTitle(value); } }
        public string Description { get { return description; } private set { SetDescription(value); } }
        public string Icon { get { return Images.First(); } }
        public ICollection<string> Images { get { return imagesPaths; } private set { SetImages(value); } }
        public ICollection<string> Audios { get { return audiosPaths; } private set { SetAudios(value); } }

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
        public Landmark(int anId, string aTitle, string aDescription, ICollection<string> paths, double lat, double lng)
        {
            SetCommonAttributes(anId,aTitle, lat, lng, aDescription);
            Images = paths;
            Audios = new List<string>();

        }
        //Constructor with images and audios lists.
        public Landmark(int anId, string aTitle, double lat, double lng, string aDescription, ICollection<string> imagesPaths, ICollection<string> audiosPaths) {
            SetCommonAttributes(anId,aTitle, lat, lng, aDescription);
            Images = imagesPaths;
            Audios = audiosPaths;
        }

        private void SetCommonAttributes(int anId, string aTitle, double lat, double lng, string aDescription) {
            Id = anId;
            Title = aTitle;
            Latitude = lat;
            Longitude = lng;
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
            imagesPaths.Add(getFileBasename(aPath));
        }

        private void SetImages(ICollection<string> pathList)
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
            imagesPaths = pathList.Select(p => getFileBasename(p)).ToList();
        }

        private void SetAudios(ICollection<string> pathList)
        {
            if (pathList == null)
            {
                throw new InvalidLandmarkException("Audios list can't be null");
            }
            if (pathList.Any(p => !File.Exists(p)))
            {
                throw new InvalidLandmarkException("Audio doesn't exist");
            }
            audiosPaths = pathList.Select(p => getFileBasename(p)).ToList();
        }

        public void AddAudio(string path) {
            if (!File.Exists(path)) {
                throw new InvalidLandmarkException("Audio doesn't exist");
            }
            audiosPaths.Add(getFileBasename(path));
        }

        private string getFileBasename(string path) {
            //checks that image exists, but only keeps image basename.
            char separator = Path.DirectorySeparatorChar;
            return path.Split(separator).Last();
        }
    }
}
