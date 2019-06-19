using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using ObligatorioISP.Services.Contracts.Dtos;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    public class AudiosController : Controller
    {
        private string landmarksAudiosDirectory;

        public AudiosController(IConfiguration configuration)
        {
            landmarksAudiosDirectory = configuration["Audios:Uri"];
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetLandmarkAudio(string id)
        {
            IActionResult result;
            try
            {
                string path = $"{landmarksAudiosDirectory}/{id}";
                FileStream image = System.IO.File.OpenRead(path);
                result = File(image, "audio/mp3");
            }
            catch (IOException e)
            {
                result = NotFound(new ErrorDto() { ErrorMessage = "Audio not found" });
            }
            return result;
        }
    }
}
