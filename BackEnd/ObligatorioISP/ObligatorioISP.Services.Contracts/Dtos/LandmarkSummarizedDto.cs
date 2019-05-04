using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.Services.Contracts.Dtos
{
    public class LandmarkSummarizedDto
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public string IconBase64 { get; set; }
    }
}
