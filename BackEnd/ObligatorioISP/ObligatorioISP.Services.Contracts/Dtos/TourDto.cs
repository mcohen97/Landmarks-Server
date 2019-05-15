using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.Services.Contracts.Dtos
{
    public class TourDto
    {
        public int Id { get; set; }
        public string Title { get; set; }
        public IEnumerable<int> LandmarksIds { get; set; }
        public string Category { get; set; } 
        public string ImageBase64 { get; set; }

    }
}
