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
        public IActionResult Get(double leftBottomLat, double leftBottomLng, double distanceInKm)
        {
            ICollection<LandmarkDto> retrieved = landmarks.GetWithinZone( leftBottomLat, leftBottomLng, distanceInKm);
            return Ok(retrieved);
        }
    }
}
