using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.DataAccess.Contracts.Dtos;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LandmarksController : ControllerBase
    {
        private ILandmarksRepository landmarks;
        public LandmarksController(ILandmarksRepository landmarksRepo) {
            landmarks = landmarksRepo;
        }

        [HttpGet]
        public IActionResult Get(double leftBottomLat, double leftBottomLng, double topRightLat, double topRightLng)
        {
            ICollection<LandmarkDto> retrieved = landmarks.GetWithinCoordenates( leftBottomLat, leftBottomLng, topRightLat,topRightLng);
            return Ok(retrieved);
        }
    }
}
