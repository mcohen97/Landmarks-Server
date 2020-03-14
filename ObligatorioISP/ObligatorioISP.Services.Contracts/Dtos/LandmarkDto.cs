﻿using System.Collections.Generic;

namespace ObligatorioISP.Services.Contracts.Dtos
{
    public class LandmarkDto
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public string Description { get; set; }
        public ICollection<string> ImageFiles { get; set; }
        public ICollection<string> AudioFiles { get; set; }
    }
}
